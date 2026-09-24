# AntiPopup Player Guide

## Introduction

AntiPopup hides Minecraft's blue unsafe-server warning popup when a native
client joins. Build `009` passed its vanilla 26.3 popup and join/rejoin check on
the test server. It performs only that join-time operation.

## How Players Use It

1. Join the server directly with the native 26.3 client.
2. Continue playing normally after the popup-free login.

There is no command, menu, permission, configuration, or per-player toggle.
Suppression is always active while the plugin is loaded.

## Available Feature

- Hides the unsafe-server popup during the supported native 26.3 login.
- Does not intercept player chat or provide any other gameplay feature.

## Requirements and Limits

AntiPopup has no rewards, costs, cooldowns, usage limits, progression, or
placeholders. Build `009` targets experimental Paper 26.3 build 40. Its popup
listener is unchanged from the working 26.2 line, but its updated packet
transport passed the native 26.3 popup check; ordinary chat remains unverified. Older
clients, protocol translators, and proxy paths are outside its supported scope.

## Important Notes

- If the popup returns, tell staff the exact Paper build, Java version, client
  version, and AntiPopup JAR name.
- A future Paper or Minecraft version requires a new candidate build and a new
  native-client login test before it is considered supported.
- Players do not need to install a companion mod or plugin.

## Technical Documentation

Administrators and developers can use the
[AntiPopup technical overview](https://github.com/mrfdev/AntiPopup/blob/master/README.md).
The canonical public page is
[docs.1moreblock.com/custom-server-plugins/antipopup/](https://docs.1moreblock.com/custom-server-plugins/antipopup/).
