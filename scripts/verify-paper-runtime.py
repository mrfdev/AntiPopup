#!/usr/bin/env python3
"""Verify the release JAR on both recorded JDKs using disposable Paper copies."""

import argparse
import hashlib
import json
import os
from pathlib import Path
import re
import shutil
import socket
import struct
import subprocess
import threading
import time


ROOT = Path(__file__).resolve().parents[1]


def properties(path):
    return dict(line.split("=", 1) for line in path.read_text().splitlines()
                if "=" in line and not line.startswith("#"))


def sha256(path):
    with path.open("rb") as stream:
        return hashlib.file_digest(stream, "sha256").hexdigest()


def varint(value):
    result = bytearray()
    while value > 127:
        result.append((value & 127) | 128)
        value >>= 7
    result.append(value)
    return bytes(result)


def read_varint(stream):
    value = 0
    for shift in range(0, 35, 7):
        byte = stream.read(1)
        if not byte:
            raise EOFError("Incomplete server status response")
        value |= (byte[0] & 127) << shift
        if byte[0] < 128:
            return value
    raise ValueError("Invalid server status VarInt")


def server_status(port):
    with socket.create_connection(("127.0.0.1", port), timeout=10) as connection:
        host = b"127.0.0.1"
        # Status handshakes accept a client protocol independent of login.
        handshake = b"\x00" + varint(0) + varint(len(host)) + host
        handshake += struct.pack(">H", port) + b"\x01"
        connection.sendall(varint(len(handshake)) + handshake + b"\x01\x00")
        with connection.makefile("rb") as stream:
            length = read_varint(stream)
            if length > 1024 * 1024 or read_varint(stream) != 0:
                raise ValueError("Invalid server status packet")
            text_length = read_varint(stream)
            if text_length > length:
                raise ValueError("Invalid server status string")
            return json.loads(stream.read(text_length))


def verify_runtime(args, facts, artifact, jdk, expected, output):
    env = dict(os.environ, JAVA_HOME=str(jdk))
    env["PATH"] = str(jdk / "bin") + os.pathsep + env.get("PATH", "")
    version_output = subprocess.check_output(
        ["java", "-version"], env=env, stderr=subprocess.STDOUT, text=True)
    if not re.search(r'version "' + re.escape(expected) + r'"', version_output):
        raise RuntimeError(f"Expected JDK {expected}: {version_output}")
    instance = output / f"jdk-{expected}"
    instance.mkdir()
    for directory in ("cache", "libraries", "versions"):
        source = args.paper_home / directory
        if source.is_dir():
            shutil.copytree(source, instance / directory)
    for name in (facts["paperJarName"], "eula.txt"):
        shutil.copy2(args.paper_home / name, instance / name)
    (instance / "plugins").mkdir()
    deployed = instance / "plugins" / artifact.name
    shutil.copy2(artifact, deployed)
    if sha256(deployed) != sha256(artifact):
        raise RuntimeError("Deployed plugin checksum mismatch")
    with socket.socket() as port_probe:
        port_probe.bind(("127.0.0.1", 0))
        port = port_probe.getsockname()[1]
    (instance / "server.properties").write_text(
        f"server-ip=127.0.0.1\nserver-port={port}\n"
        "online-mode=true\nenforce-secure-profile=true\n"
        "view-distance=2\nsimulation-distance=2\nmax-players=2\n"
        "level-name=smoke-world\nlevel-seed=1\n")
    (instance / "java-version.txt").write_text(version_output)
    command = ["java", "-Xms512M", "-Xmx1G", "-XX:ActiveProcessorCount=2",
               "-Dterminal.jline=false", "-Dterminal.ansi=false",
               "-jar", facts["paperJarName"], "--nogui"]
    process = subprocess.Popen(command, cwd=instance, env=env, stdin=subprocess.PIPE,
                               stdout=subprocess.PIPE, stderr=subprocess.STDOUT,
                               text=True, bufsize=1)
    lines = []

    def capture():
        with (instance / "console.log").open("w") as log:
            for line in process.stdout:
                lines.append(line)
                log.write(line)
                log.flush()

    reader = threading.Thread(target=capture, daemon=True)
    reader.start()

    def wait_for(fragment, start=0, timeout=5):
        deadline = time.monotonic() + timeout
        while time.monotonic() < deadline:
            if fragment in "".join(lines[start:]):
                return
            if process.poll() is not None:
                break
            time.sleep(0.1)
        raise RuntimeError(f"Missing {fragment!r}; inspect {instance / 'console.log'}")

    def console(command_text, expected_text):
        start = len(lines)
        process.stdin.write(command_text + "\n")
        process.stdin.flush()
        wait_for(expected_text, start, 30)

    try:
        wait_for('Done (', timeout=180)
        wait_for("Loaded embedded PacketEvents.")
        wait_for(f"Initiated embedded PacketEvents for Paper {facts['paperTarget']}.")
        wait_for(f"artifact={artifact.name}, compiledPaperApi={facts['paperApiVersion']}, javaTarget=25")
        wait_for(f"with Java {expected}+")
        wait_for(f"Paper version {facts['paperTarget']}-{facts['paperBuild']}-")
        print(f"JDK {expected}: Paper started and AntiPopup enabled", flush=True)
        console("plugins", "AntiPopup")
        console("version AntiPopup", f"AntiPopup version {facts['pluginVersion']}-{facts['pluginBuild']}")
        console("antipopup", "Unknown or incomplete command")
        status = server_status(port)
        if facts["paperTarget"] not in status["version"]["name"]:
            raise RuntimeError(f"Unexpected status version: {status}")
        if "preventsChatReports" in status:
            raise RuntimeError("Removed chat-report status marker returned")
        (instance / "status.json").write_text(json.dumps(status, indent=2) + "\n")
        print(f"JDK {expected}: listing, version, command absence and status checks passed", flush=True)
    finally:
        if process.poll() is None:
            process.stdin.write("stop\n")
            process.stdin.flush()
            try:
                process.wait(timeout=60)
            except subprocess.TimeoutExpired:
                process.terminate()
                try:
                    process.wait(timeout=10)
                except subprocess.TimeoutExpired:
                    process.kill()
                    process.wait()
                raise RuntimeError(f"Paper did not stop cleanly: {instance}")
        reader.join(timeout=10)
    log = "".join(lines)
    if process.returncode != 0 or "Disabled embedded PacketEvents." not in log:
        raise RuntimeError(f"Unclean shutdown: {instance}")
    if "All RegionFile I/O tasks to complete" not in log:
        raise RuntimeError(f"Incomplete server shutdown: {instance}")
    errors = [line.strip() for line in lines if re.search(r"\bERROR[\]:]", line)
              or "Exception" in line or "UnsupportedClassVersionError" in line]
    if errors:
        raise RuntimeError(f"Runtime errors: {errors}")
    if (instance / "plugins" / "AntiPopup").exists():
        raise RuntimeError("AntiPopup created persistent plugin state")
    result = {"jdk": expected, "javaVersion": version_output.strip(),
              "artifact": artifact.name, "sha256": sha256(artifact),
              "paperBuild": facts["paperBuild"], "exitCode": process.returncode,
              "checks": ["startup", "plugin-load", "runtime-metadata", "plugin-list",
                         "plugin-version", "command-absence", "server-status",
                         "no-plugin-state", "clean-shutdown"],
              "warnings": [line.strip() for line in lines if "WARN" in line]}
    (instance / "result.json").write_text(json.dumps(result, indent=2) + "\n")
    print(f"JDK {expected}: PASS, clean shutdown (exit 0)", flush=True)
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--paper-home", type=Path, required=True,
                        help="Existing Paper template with the pinned JAR and accepted eula.txt")
    parser.add_argument("--jdk25-home", type=Path, required=True)
    parser.add_argument("--jdk26-home", type=Path, required=True)
    args = parser.parse_args()
    facts = properties(ROOT / "gradle.properties")
    if sha256(args.paper_home / facts["paperJarName"]) != facts["paperJarSha256"]:
        parser.error("Paper template checksum does not match gradle.properties")
    if properties(args.paper_home / "eula.txt").get("eula") != "true":
        parser.error("The template must already contain an accepted eula.txt")
    release = f"{facts['pluginVersion']}-{facts['pluginBuild']}"
    artifact = ROOT / "build" / "libs" / (
        f"1MB-AntiPopup-v{release}-j{facts['javaTarget']}-{facts['paperTarget']}.jar")
    if not artifact.is_file():
        parser.error("Run the canonical full rebuild before runtime verification")
    output = ROOT / "run" / "verification" / release / time.strftime("%Y%m%d-%H%M%S")
    output.mkdir(parents=True)
    print(f"Verification records: {output}", flush=True)
    results = [verify_runtime(args, facts, artifact, jdk.resolve(), expected, output)
               for jdk, expected in [(args.jdk25_home, facts["runtimeJdk25Version"]),
                                     (args.jdk26_home, facts["runtimeJdk26Version"])]]
    (output / "results.json").write_text(json.dumps(results, indent=2) + "\n")


if __name__ == "__main__":
    main()
