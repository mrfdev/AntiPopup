# AntiPopup Player Guide

## Introduction

AntiPopup hides Minecraft's unsafe-server warning popup on 1MoreBlock and limits
the vanilla reportable-chat path. It runs automatically while you play; there is
no setup, purchase, or menu required from players.

## How Players Use It

1. Join the server normally.
2. Chat as usual.
3. Run `/antipopup info` whenever you want to confirm the installed version or
   open this guide from its clickable link.

AntiPopup handles the relevant chat and server-information packets in the
background. Staff control its server-wide settings.

## Available Features

- Hides the unsafe-server popup when the server configuration enables that protection.
- Marks compatible server-list information as preventing chat reports.
- Limits the normal reportable player-chat path when chat-report blocking is enabled.
- Keeps the protection automatic for every player; there is no per-player toggle.

## Quick Start

- Run `/antipopup info`.
- Click the documentation URL in the response.
- Continue using chat normally.

Running `/antipopup` with no subcommand shows the same overview.

## Commands

| Command | What it does |
| --- | --- |
| `/antipopup` | Shows the AntiPopup overview. |
| `/antipopup info` | Shows the player-facing name, purpose, installed version, starting command, and clickable documentation URL. |

The setup and reload operations are local-console administration tools and are
not player commands.

## Permissions or Rank Requirements

`/antipopup` and `/antipopup info` use the `antipopup.commands` permission.
It defaults to everyone, so no rank is normally required. A server permission
configuration can still explicitly deny it.

## Rewards, Costs, Limits, and Cooldowns

AntiPopup has no rewards, currency or item costs, usage limits, cooldowns, or
progression. Its protections apply server-wide according to staff configuration.

## Placeholders

AntiPopup does not provide PlaceholderAPI placeholders.

## Important Notes

- AntiPopup protects the server's chat path; it is not a promise that every
  third-party chat or protocol plugin combination will behave identically.
- You do not need to run setup or reload commands.
- If the warning popup or reporting behavior unexpectedly returns, tell staff
  which client version you use and whether the server is translating it through
  ViaVersion.
- Chat formatting may be controlled by other server plugins.

## Related Features

Paper supplies the server chat implementation. ViaVersion may translate network
protocols for clients on different versions, but those combinations are managed
separately from AntiPopup.

## Technical Documentation

Administrators and developers can use the
[AntiPopup technical overview](https://github.com/mrfdev/AntiPopup/blob/master/README.md).
The canonical public page is
[docs.1moreblock.com/custom-server-plugins/antipopup/](https://docs.1moreblock.com/custom-server-plugins/antipopup/).
