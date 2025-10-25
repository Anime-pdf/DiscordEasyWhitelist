# DiscordEasyWhitelist

## Table of Contents

- [Overview](#overview)
- [Slash Commands](#slash-commands)
- [Configuration](#configuration)
- [Installation](#installation)
- [Examples](#examples)

---

# Overview
DiscordEasyWhitelist provides both simple access to the SimpleWhitelist and advanced features for managing player access to the server. Nearly all features can be disabled or modified, allowing for flexible customization.

---

# Slash Commands
> [!NOTE]
> `[variables]` in square brackets **are not** required  
> `<variables>` in angle brackets **are** required

## /whitelist
> [!NOTE]
> **Requires moderator role to access**

Provides access to SimpleWhitelist
### `/whitelist check <nickname>`
* Checks if SimpleWhitelist contains specified nickname
### `/whitelist add <nickname>`
* Adds specified `nickname` to SimpleWhitelist
### `/whitelist remove <nickname>`
* Removed specified `nickname` from SimpleWhitelist

## /link
> [!NOTE]
> **Requires moderator role to access**

Provides access to DiscordSRV's linking feature
### `/link check [user] [nickname]`
* Checks if `user` **OR** `nickname`. Only one argument is needed
### `/link add <user> <nickname>`
* Links `user` with `nickanme` if both of them aren't linked yet
### `/link remove [user] [nickname]`
* Removes link from `user` **OR** `nickname`. Only one argument is needed

## /accept
> [!NOTE]
> **Requires moderator role to access**

### `/accept <user> <nickname>`
Complex comand that can execute following tasks if configured accordingly:
* Link `user` to `nickname`
* Add `nickname` to SimpleWhitelist
* Change `user`'s guild name to `nickname`
* Add specified list of roles to `user`
* Remove specified list of roles from `user`
* Send welcome message in specified channel

## /remove
> [!NOTE]
> **Requires moderator role to access**

### `/remove <user> [nickname] [reason]`
> [!IMPORTANT]
> `nickname` is optional argument because command can execute series of checks to determine `nickname` on it's own if it wasn't provided.
> Details on this will be provided later in [Configuration](#-configuration) section

Complex comand that can execute following tasks if configured accordingly:
* Unlink `user` from any in-game nickname
* Remove `nickname` from SimpleWhitelist
* Kick from game if `nickname` is on the server
* Ban `user` from guild
* Send remove message in specified channel
* Send direct message to `user`

## /status
> [!NOTE]
> **Does NOT require moderator role to access**

### `/status`
* Prints amount of players online, list of them, while annotation linked players with discord mention

## /reload
> [!NOTE]
> **Requires moderator role to access**

### `/reload`
* Reloads config options from files

# Configuration
Plugin uses 2 files to configurate itself:
* `general.yml` - Contains options to disable/enable certain funmctionality, but also contains role and channel IDs
* `langugae.yml` - Contains all possible feedback messages plugin can send
> [!IMPORTANT]
> If files doesn't exist (e.g. on first start), `general.yml`, `langugae.yml` **AND** `langugae.russian.yml` will be generated.  
> `langugae.russian.yml` is `langugae.yml` translated to russian langugae

## `general.yml`
### `moderator-roles`
* Type: List of strings
* Description: List of discord role IDs that will be treated as moderators
---
### `whitelist`
#### `enable`
* Type: Boolean
* Description: If `/whitelist` slash command will be enabled
---
### `link`
#### `enable`
* Type: Boolean
* Description: If `/link` slash command will be enabled
---
### `accept`
#### `enable`
* Type: Boolean
* Description: If `/accept` slash command will be enabled
#### `link-to-nickname`
* Type: Boolean
* Description: If `user` should be linked to `nickname`
#### `add-to-whitelist`
* Type: Boolean
* Description: If `nickname` should be added to SimpleWhitelist
#### `change-guild-name`
* Type: Boolean
* Description: If `user`'s guild name should be changed to `nickname`
#### `add-roles`
* Type: Boolean
* Description: If bot should add roles to `user`
#### `roles-to-add`
* Type: List of strings
* Description: List of role IDs that should be added to `user`
#### `remove-roles`
* Type: Boolean
* Description: If bot should remove roles from `user`
#### `roles-to-remove`
* Type: List of strings
* Description: List of role IDs that should be removed from `user`
#### `send-welcome-message`
* Type: Boolean
* Description: If bot should send welcome message to specified channel
#### `welcome-channel-id`
* Type: String
* Description: Channel ID where welcome message should be sent
---
### `remove`
#### `enable`
* Type: Boolean
* Description: If `/remove` slash command will be enabled
#### `unlink-from-nickname`
* Type: Boolean
* Description: If `user` should be unlinked from any linked nickname
#### `fallback-to-guild-username`
* Type: Boolean
* Description: If `/remove` should fallback to guild name if it can't determine nickname from linking.  
  `/remove` uses series of checks to determine nickname if it wasn't provided amnually, here is an overview of the algorithm:
  ```
  If moderator entered a nickname
      Check if nickname linked to discord
          - Linked to this user -> unlink, proceed
          - Nickname or discord linked to someone else -> error
          - Not linked -> warning, proceed
  If moderator didn't enter a nickname
      Check if discord linked to nickname
          If linked, try to resolve nickname
              - Nickname can be resolved -> unlink, proceed
              - Nickname can't be resolved -> warning, proceed
          If not linked, check if we can fallback
              - Fallback isn't turned on -> error
              - Fallback turned on -> warning, fallback to discord name, proceed
  ```
#### `remove-from-whitelist`
* Type: Boolean
* Description: If `nickname` should be removed from SimpleWhitelist
#### `kick-from-game-if-online`
* Type: Boolean
* Description: If plugin should kick any player with `nickname` that is on the server
#### `send-direct-message`
* Type: Boolean
* Description: If bot should send direct message to `user`
#### `send-remove-message`
* Type: Boolean
* Description: If bot should send remove message to specified channel
#### `remove-channel-id`
* Type: String
* Description: Channel ID where remove message should be sent
#### `ban-from-guild`
* Type: Boolean
* Description: If bot should ban `user` from guild
---
### `leave`
#### `enable`
* Type: Boolean
* Description: If bot should track user leaving from guild and execute tasks
#### `unlink-if-available`
* Type: Boolean
* Description: If `user` should be unlinked from any linked nickname
#### `remove-from-whitelist`
* Type: Boolean
* Description: If `nickname` should be removed from SimpleWhitelist
#### `kick-from-game-if-online`
* Type: Boolean
* Description: If plugin should kick any player with `nickname` that is on the server
#### `ban-from-guild`
* Type: Boolean
* Description: If bot should ban `user` from guild
#### `send-direct-message`
* Type: Boolean
* Description: If bot should send direct message to `user`
#### `send-leave-message`
* Type: Boolean
* Description: If bot should send remove message to specified channel
#### `leave-channel-id`
* Type: String
* Description: Channel ID where remove message should be sent
#### `send-report-message`
* Type: Boolean
* Description: If bot should send report message to specified channel.  
  Since bot can't repond to any slash-command, it can, and should, send report message what tasks he executed
#### `report-channel-id`
* Type: String
* Description: Channel ID where report message should be sent
---
### `status`
#### `enable`
* Type: Boolean
* Description: If `/status` slash command will be enabled

## `language.yml`

This file defines all messages the plugin can send in Discord  
It supports placeholders and can contain multi-line lists for messages with multiple lines.

> [!IMPORTANT]
> If file doesn't exist, `language.yml` and `language.russian.yml` will be generated automatically.  
> `language.russian.yml` is a full translation of this file into Russian language.

---

### `error-prefix`
* Type: String
* Description: Prefix added to all error messages

---

### `warning-prefix`
* Type: String
* Description: Prefix added to all warning messages

---

### `success-prefix`
* Type: String
* Description: Prefix added to all success messages

---

### `general`
#### `unknown-error`
* Type: String
* Description: Sent when an unexpected error occurs

#### `no-permission`
* Type: String
* Description: Sent when user doesn't have permission to use the command

#### `wrong-command-argument`
* Type: String
* Description: Sent when command argument is invalid
* Placeholders:
  - `{command}` - command used
  - `{arg}` - name of argument

#### `no-command-argument`
* Type: String
* Description: Sent when user doesn’t provide required arguments

#### `too-many-command-arguments`
* Type: String
* Description: Sent when too many arguments are provided

#### `channel-not-found`
* Type: String
* Description: Sent when channel ID from config can’t be resolved
* Placeholders:
  - `{channel_id}` - ID of channel that wasn't found

#### `user-not-on-server`
* Type: String
* Description: Sent when user is not a member of the Discord guild
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

---

### `accept`
#### `disabled`
* Type: String
* Description: Sent when `/accept` command is disabled

#### `link-warning-already-others`
* Type: String
* Description: Shown when nickname or user is already linked to someone else
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID
  - `{nickname}` - minecraft nickname

#### `link-warning-already-same`
* Type: String
* Description: Sent when user is already linked to the same nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID
  - `{nickname}` - minecraft nickname

#### `link-success`
* Type: String
* Description: Sent when user was successfully linked to nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID
  - `{nickname}` - minecraft nickname

#### `whitelist-warning-already`
* Type: String
* Description: Sent when nickname is already in whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `whitelist-success`
* Type: String
* Description: Sent when nickname was added to whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `guild-name-success`
* Type: String
* Description: Sent when user’s guild name was changed to nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `guild-role-remove-success`
* Type: String
* Description: Sent when roles were removed from user
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{roles}` - formatted string that will contain all mentions of all removed roles

#### `guild-role-add-success`
* Type: String
* Description: Sent when roles were added to user
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{roles}` - formatted string that will contain all mentions of all added roles

#### `welcome-message`
* Type: List of strings
* Description: Multi-line message sent to welcome channel
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname
  - `{moderator_mention}` - formatted string that will mention moderator
  - `{moderator_username}` - discord username of the moderator
  - `{moderator_name}` - discord name of the moderator
  - `{moderator_id}` - discord ID of the moderator

#### `welcome-message-success`
* Type: String
* Description: Confirmation message that welcome message was sent
* Placeholders:
  - `{channel_id}` - ID of channel where message was sent

#### `accept-success`
* Type: String
* Description: Final summary log message
* Placeholders:
  - `{report}` - formatted message that contains list of executed tasks

---

### `remove`
#### `disabled`
* Type: String
* Description: Sent when `/remove` command is disabled

#### `link-error-other`
* Type: String
* Description: Shown when nickname or user are already linked to someone else
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `link-error-never-played`
* Type: String
* Description: Shown when user never joined server and nickname can’t be resolved
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `link-error-not-linked`
* Type: String
* Description: Shown when user isn’t linked to any nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `link-warning-not-linked`
* Type: String
* Description: Warning when user isn’t linked to provided nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `link-warning-not-linked-fallback`
* Type: String
* Description: Warning when user isn’t linked and fallback nickname is used
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{fallback}` - fallback name that will be used as minecraft nickname

#### `link-success`
* Type: String
* Description: Sent when unlinking succeeded
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID
  - `{nickname}` - minecraft nickname

#### `whitelist-warning-already`
* Type: String
* Description: Sent when nickname is already missing from whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `whitelist-success`
* Type: String
* Description: Sent when nickname was removed from whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `kick-message`
* Type: Component
* Description: Message that will be shown to kicked player

#### `remove-message`
* Type: List of strings
* Description: Message template sent in Discord remove channel
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname
  - `{moderator_mention}` - formatted string that will mention moderator
  - `{moderator_username}` - discord username of the moderator
  - `{moderator_name}` - discord name of the moderator
  - `{moderator_id}` - discord ID of the moderator

#### `remove-message-reason`
* Type: List of strings
* Description: Message with reason sent in remove channel
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname
  - `{moderator_mention}` - formatted string that will mention moderator
  - `{moderator_username}` - discord username of the moderator
  - `{moderator_name}` - discord name of the moderator
  - `{moderator_id}` - discord ID of the moderator
  - `{reason}` - reason of removal

#### `remove-message-success`
* Type: String
* Description: Confirmation message for sent remove message
* Placeholders:
  - `{channel_id}` - ID of channel where message was sent

#### `remove-direct-message`
* Type: List of strings
* Description: DM message sent to user

#### `remove-direct-message-reason`
* Type: List of strings
* Description: DM message with reason sent to user
* Placeholders:
  - `{reason}` - reason of removal

#### `remove-direct-message-success`
* Type: String
* Description: Confirmation that DM was sent successfully
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `remove-direct-message-failure`
* Type: String
* Description: Sent when user has closed DMs
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `guild-ban-reason`
* Type: String
* Description: Reason used for Discord guild ban

#### `guild-ban-success`
* Type: String
* Description: Confirmation that user was banned from guild
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `remove-success`
* Type: String
* Description: Final log message
* Placeholders:
  - `{report}` - formatted message that contains list of executed tasks

---

### `whitelist`
#### `disabled`
* Type: String
* Description: Sent when `/whitelist` command is disabled
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `contains-nickname`
* Type: String
* Description: Sent when nickname exists in whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `not-contains-nickname`
* Type: String
* Description: Sent when nickname doesn’t exist in whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `added-nickname`
* Type: String
* Description: Sent when nickname added successfully
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `not-added-nickname`
* Type: String
* Description: Sent when nickname was already present
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `removed-nickname`
* Type: String
* Description: Sent when nickname was removed successfully
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `not-removed-nickname`
* Type: String
* Description: Sent when nickname was already missing from whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

---

### `link`
#### `disabled`
* Type: String
* Description: Sent when `/link` command is disabled

#### `nickname-not-linked`
* Type: String
* Description: Sent when nickname isn’t linked to anyone
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `nickname-linked`
* Type: String
* Description: Sent when nickname is linked to user
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `user-not-linked`
* Type: String
* Description: Sent when user isn’t linked to any nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `user-linked`
* Type: String
* Description: Sent when user is linked to nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `user-linked-no-nickname`
* Type: String
* Description: Sent when user is linked by UUID but nickname can’t be resolved
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID

#### `link-warning-already-others`
* Type: String
* Description: Warning when nickname or user already linked to others
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID
  - `{nickname}` - minecraft nickname

#### `link-warning-already-same`
* Type: String
* Description: Warning when user already linked to same nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID
  - `{nickname}` - minecraft nickname

#### `link-success`
* Type: String
* Description: Sent when linking succeeds
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID
  - `{nickname}` - minecraft nickname

#### `unlink-nickname-not-found`
* Type: String
* Description: Sent when nickname not found in linking database
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `unlink-nickname-success`
* Type: String
* Description: Sent when nickname successfully unlinked from user
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `unlink-user-not-found`
* Type: String
* Description: Sent when user not linked to any nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `unlink-user-success-no-nickname`
* Type: String
* Description: Sent when user unlinked from UUID without nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID

#### `unlink-user-success`
* Type: String
* Description: Sent when user unlinked from nickname successfully
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

---

### `reload`
#### `reloaded`
* Type: String
* Description: Sent when plugin config was reloaded successfully

---

### `leave`
#### `leave-message`
* Type: List of strings
* Description: Message sent to leave channel when user leaves server
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `leave-message-name-not-resolved`
* Type: List of strings
* Description: Message when player nickname can’t be resolved
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `leave-direct-message`
* Type: List of strings
* Description: DM message sent to user upon leaving

#### `guild-ban-reason`
* Type: String
* Description: Reason used for Discord guild ban

#### `report-message`
* Type: String
* Description: Detailed report message containing
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{report}` - formatted string that contains all executed tasks

#### `report-warning-not-linked`
* Type: String
* Description: Warning when user wasn’t linked
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `report-warning-name-not-resolved`
* Type: String
* Description: Warning when nickname couldn’t be resolved
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `report-unlinked`
* Type: String
* Description: Confirmation when user unlinked from nickname
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

#### `report-unlinked-name-not-resolved`
* Type: String
* Description: Confirmation when unlinked but nickname unresolved
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{uuid}` - minecraft UUID

#### `report-removed-from-whitelist`
* Type: String
* Description: Confirmation when nickname removed from whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `report-warning-not-in-whitelist`
* Type: String
* Description: Warning when nickname wasn’t in whitelist
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `kick-message`
* Type: Component
* Description: Message shown in-game to kicked player

#### `report-banned-from-guild`
* Type: String
* Description: Confirmation when user was banned from guild
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `report-warning-already-banned`
* Type: String
* Description: Warning when user already banned
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `report-message-success`
* Type: String
* Description: Confirmation that leave message sent successfully
* Placeholders:
  - `{channel_id}` - ID of channel where report message was sent

#### `report-direct-message-success`
* Type: String
* Description: Confirmation that DM sent successfully
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user

#### `report-warning-direct-messages-failure`
* Type: String
* Description: Warning when DM sending failed (closed DMs)
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
---

### `status`
#### `disabled`
* Type: String
* Description: Sent when `/status` command is disabled

#### `status-message-no-players`
* Type: String
* Description: Shown when no players are online

#### `status-message`
* Type: String
* Description: Template for online players list
* Placeholders:
  - `{players_count}` - amount of players online
  - `{players}` - formatted message that contains list of players online

#### `status-entry`
* Type: String
* Description: Template for single player entry (not linked)
* Placeholders:
  - `{nickname}` - minecraft nickname

#### `status-entry-linked`
* Type: String
* Description: Template for single player entry (linked to Discord)
* Placeholders:
  - `{discord_mention}` - formatted string that will mention user
  - `{discord_username}` - discord username of the user
  - `{discord_name}` - discord name of the user
  - `{discord_id}` - discord ID of the user
  - `{nickname}` - minecraft nickname

# Installation
You will need 2 plugins installed along:

* SimpleWhitelist
* DiscordSRV

That's it! DiscordEasyWhitelist should work as drop-in addon for them

# Examples

## `general.yml`

```yaml
moderator-roles:
- '0000000000000000000'
- '0000000000000000000'
whitelist:
  enable: true
link:
  enable: true
accept:
  enable: true
  link-to-nickname: true
  add-to-whitelist: true
  change-guild-name: true
  add-roles: true
  roles-to-add:
  - '0000000000000000000'
  - '0000000000000000000'
  remove-roles: true
  roles-to-remove:
  - '0000000000000000000'
  - '0000000000000000000'
  send-welcome-message: true
  welcome-channel-id: '0000000000000000000'
remove:
  enable: true
  unlink-from-nickname: true
  fallback-to-guild-username: false
  remove-from-whitelist: true
  kick-from-game-if-online: true
  send-remove-message: true
  remove-channel-id: '0000000000000000000'
  send-direct-message: true
  ban-from-guild: true
leave:
  enable: false
  unlink-if-available: true
  remove-from-whitelist: true
  kick-from-game-if-online: true
  ban-from-guild: true
  send-leave-message: true
  leave-channel-id: '0000000000000000000'
  send-direct-message: true
  send-report-message: true
  report-channel-id: '0000000000000000000'
status:
  enable: true
```
## `langugae.yml`

```yaml
error-prefix: '🚫 '
warning-prefix: '⚠️ '
success-prefix: '✅ '
general:
  unknown-error: Unknown error. Check linking status of nickname/user being used
  no-permission: You don't have permission to use this command
  wrong-command-argument: 'Invalid argument: `{arg}`'
  no-command-argument: Enter at least one argument
  too-many-command-arguments: Too many arguments entered
  only-on-server: This command can only be used on discord server
  channel-not-found: Channel `{channel_id}` not found, message won't be sent
  user-not-on-server: '{discord_mention} is not in the guild'
accept:
  disabled: This functionality is disabled
  link-warning-already-others: Nickname or user are already linked to someone else,
    try unlinking them first
  link-warning-already-same: '{discord_mention} already linked to `{nickname}`'
  link-success: '{discord_mention} was linked to `{nickname}`'
  whitelist-warning-already: Nickname `{nickname}` already persists in the whitelist
  whitelist-success: Nickname `{nickname}` was added to the whitelist
  guild-name-success: '{discord_mention}''s guild name was changed to `{nickname}`'
  guild-role-remove-success: Roles {roles} were removed
  guild-role-add-success: Roles {roles} were added
  welcome-message:
  - '{discord_mention} Welcome!'
  - You was added to the server, IP can be found in <#0000000000000000000>, or using
    command `!ip`. Minecraft version 1.21.8.
  - Yoy should also check out <#0000000000000000000>, it contains useful information
    to start playing on the server comfortably. Have fun!
  - ''
  - '*Application review by {moderator_mention}*'
  welcome-message-success: Welcome message was sent into <#{channel_id}>
  accept-success: |-
    Logs:
    {report}
remove:
  disabled: This functionality is disabled
  link-error-other: Nickname or user are already linked to someone else, try unlinking
    them first
  link-error-never-played: '{discord_mention} never entered the server, can''t resolve
    their nickname, enter it manually'
  link-error-not-linked: '{discord_mention} isn''t linked to any nickname'
  link-warning-not-linked: '{discord_mention} isn''t linked to entered nickname'
  link-warning-not-linked-fallback: '{discord_mention} isn''t linked to any nickname,
    falling back to `{fallback}`'
  link-success: '{discord_mention} was unlinked from `{nickname}`'
  whitelist-warning-already: Whitelist already doesn't contain `{nickname}`
  whitelist-success: Nickname `{nickname}` was removed from the whitelist
  kick-message: <red>You was removed from the whitelist
  remove-message:
  - '{discord_mention} (`{nickname}`) was banned'
  remove-message-reason:
  - '{discord_mention} (`{nickname}`) was banned with the reason: "{reason}"'
  remove-message-success: Remove message was sent into <#{channel_id}>
  remove-direct-message:
  - You was banned on the server
  remove-direct-message-reason:
  - 'You was banned on the server with a reason: "{reason}"'
  remove-direct-message-success: Direct message was sent to {discord_mention}
  remove-direct-message-failure: '{discord_mention} has closed DMs, message not sent'
  guild-ban-reason: Banned by DEW
  guild-ban-success: '{discord_mention} was banned on the server'
  remove-success: |-
    Logs:
    {report}
whitelist:
  disabled: This functionality is disabled
  contains-nickname: Whitelist contains `{nickname}`
  not-contains-nickname: Whitelist doesn't contain `{nickname}`
  added-nickname: Nickname `{nickname}` was added to the whitelist
  not-added-nickname: Whitelist already contains `{nickname}`
  removed-nickname: Nickname `{nickname}` was removed from the whitelist
  not-removed-nickname: Whitelist already doesn't contain `{nickname}`
link:
  disabled: This functionality is disabled
  nickname-not-linked: '`{nickname}` isn''t linked to any user'
  nickname-linked: '`{nickname}` is linked to {discord_mention}'
  user-not-linked: '{discord_mention} isn''t linked to any nickname'
  user-linked: '{discord_mention} is linked to `{nickname}`'
  user-linked-no-nickname: '{discord_mention}  is linked to UUID `{uuid}`, player
    didn''t join server once, can''t retrieve his nickname'
  link-warning-already-others: Nickname or user are already linked to someone else,
    try unlinking them first
  link-warning-already-same: '{discord_mention} already linked to `{nickname}`'
  link-success: '{discord_mention} was linked to `{nickname}`'
  unlink-nickname-not-found: '`{nickname}` isn''t linked to any user'
  unlink-nickname-success: '`{nickname}` was unlinked from {discord_mention}'
  unlink-user-not-found: '{discord_mention} isn''t linked to any nickname'
  unlink-user-success-no-nickname: '{discord_mention} was unlinked from UUID `{uuid}`,
    player didn''t join server once, can''t retrieve his nickname'
  unlink-user-success: '{discord_mention} was unlinked from `{nickname}`'
reload:
  reloaded: Config reloaded
leave:
  leave-message:
  - '{discord_mention} (`{nickname}`) left the server and faced consequences 💀'
  leave-message-name-not-resolved:
  - '{discord_mention} left the server and faced consequences 💀'
  leave-direct-message:
  - You left the server and was removed from the whitelist
  guild-ban-reason: Banned by DEW
  report-message: |-
    {discord_mention} left the server!
    {report}
  report-warning-not-linked: User wasn't linked to any nickname
  report-warning-name-not-resolved: Player didn't join server once, can't retrieve
    his nickname
  report-unlinked-name-not-resolved: User was unlinked from UUID `{uuid}`
  report-unlinked: User was unlinked from `{nickname}`
  report-removed-from-whitelist: '`{nickname}` was removed from the whitelist'
  report-warning-not-in-whitelist: Whitelist already didn't contain `{nickname}`
  kick-message: <red>You was removed from the whitelist
  report-banned-from-guild: '{discord_mention} was banned from the server'
  report-warning-already-banned: '{discord_mention} was already banned from the server'
  report-message-success: Leave message was sent into <#{channel_id}>
  report-direct-message-success: Direct message was sent to {discord_mention}
  report-warning-direct-messages-failure: '{discord_mention} has closed DMs, message
    not sent'
status:
  disabled: This functionality is disabled
  status-message-no-players: '## No players online'
  status-message: |-
    ## {players_count} players online:
    {players}
  status-entry: '* {nickname}'
  status-entry-linked: '* {nickname} ({discord_mention})'
```

## `language.russian.yml`
```yaml
error-prefix: '🚫 '
warning-prefix: '⚠️ '
success-prefix: '✅ '
general:
  unknown-error: Неизвестная ошибка. Проверьте привязку используемых ников/аккаунтов
  no-permission: У вас нет прав для использования этой команды
  wrong-command-argument: Аргумент `{arg}` введен некорректно
  no-command-argument: Введите хотя бы один аргумент
  too-many-command-arguments: Слишком много аргументов
  only-on-server: Эта команда может быть использована только на сервере
  channel-not-found: Канал `{channel_id}` не найден, сообщение не будет отправлено
  user-not-on-server: '{discord_mention} не находится на сервере'
accept:
  disabled: Этот функционал выключен
  link-warning-already-others: Ник или аккаунт уже привязаны к кому-то другому, сначала
    отвяжите их
  link-warning-already-same: '{discord_mention} уже привязан к `{nickname}`'
  link-success: '{discord_mention} был привязан к `{nickname}`'
  whitelist-warning-already: Ник `{nickname}` уже присутствует в вайт-листе
  whitelist-success: Ник `{nickname}` добавлен в вайт-лист
  guild-name-success: Имя {discord_mention} было изменено на `{nickname}`
  guild-role-remove-success: Роли {roles} были удалены
  guild-role-add-success: Роли {roles} были добавлены
  welcome-message:
  - '{discord_mention}, добро пожаловать!'
  - Тебя добавили на сервер. IP находится в <#1086714411848310934>, либо с помощью
    команды `!ip`. Версия 1.21.8.
  - Рекомендую также посмотреть канал <#1285560328490324008>, чтобы начать ориентироваться
    на сервере. Приятной игры!
  - ''
  - '*Анкета рассмотрена {moderator_mention}*'
  welcome-message-success: В <#{channel_id}> отправлено приветственное сообщение
  accept-success: |-
    Отчёт:
    {report}
remove:
  disabled: Этот функционал выключен
  link-error-other: Ник или аккаунт уже привязаны к кому-то другому, сначала отвяжите
    их
  link-error-never-played: '{discord_mention} никогда не заходил на сервер, невозможно
    получить ник, укажите его вручную'
  link-error-not-linked: '{discord_mention} не привязан к какому-либо нику'
  link-warning-not-linked: '{discord_mention} не привязан к указанному нику'
  link-warning-not-linked-fallback: '{discord_mention} не привязан к какому-либо нику.
    Использую `{fallback}`'
  link-success: '{discord_mention} был отвязан от `{nickname}`'
  whitelist-warning-already: Ник `{nickname}` и так не присутствует в вайт-листе
  whitelist-success: Ник `{nickname}` удалён из вайт-листа
  kick-message: <red>Вы были удалены из вайт-листа
  remove-message:
  - '{discord_mention} (`{nickname}`) был забанен'
  remove-message-reason:
  - '{discord_mention} (`{nickname}`) был забанен по причине: "{reason}"'
  remove-message-success: В <#{channel_id}> отправлено сообщение о бане
  remove-direct-message:
  - Вы были забанены на сервере
  remove-direct-message-reason:
  - 'Вы были забанены на сервере по причине: "{reason}"'
  remove-direct-message-success: '{discord_mention} отправлено личное сообщение'
  remove-direct-message-failure: У {discord_mention} закрыты личные сообщения, сообщение
    не отправлено
  guild-ban-reason: Забанен DEW
  guild-ban-success: '{discord_mention} забанен на сервере'
  remove-success: |-
    Отчёт:
    {report}
whitelist:
  disabled: Этот функционал выключен
  contains-nickname: Ник `{nickname}` присутствует в вайт-листе
  not-contains-nickname: Ник `{nickname}` не присутствует в вайт-листе
  added-nickname: Ник `{nickname}` добавлен в вайт-лист
  not-added-nickname: Ник `{nickname}` уже есть в вайт-листе
  removed-nickname: Ник `{nickname}` удалён из вайт-листа
  not-removed-nickname: Ник `{nickname}` и так не присутствует в вайт-листе
link:
  disabled: Этот функционал выключен
  nickname-not-linked: '`{nickname}` не привязан к какому-либо аккаунту'
  nickname-linked: '`{nickname}` привязан к {discord_mention}'
  user-not-linked: '{discord_mention} не привязан к какому-либо нику'
  user-linked: '{discord_mention} привязан к `{nickname}`'
  user-linked-no-nickname: '{discord_mention} привязан к UUID `{uuid}`, игрок ни разу
    не заходил на сервер, поэтому вычислить ник невозможно'
  link-warning-already-others: Ник или аккаунт уже привязаны к кому-то другому, сначала
    отвяжите их
  link-warning-already-same: '{discord_mention} уже привязан к `{nickname}`'
  link-success: '{discord_mention} был привязан к `{nickname}`'
  unlink-nickname-not-found: '`{nickname}` не привязан к какому-либо аккаунту'
  unlink-nickname-success: '`{nickname}` был отвязан от {discord_mention}'
  unlink-user-not-found: '{discord_mention} не привязан к какому-либо нику'
  unlink-user-success-no-nickname: '{discord_mention} был отвязан от UUID `{uuid}`,
    игрок ни разу не заходил на сервер, поэтому вычислить ник невозможно'
  unlink-user-success: '{discord_mention} был отвязан от `{nickname}`'
reload:
  reloaded: Конфиг перезагружен
leave:
  leave-message:
  - '{discord_mention} (`{nickname}`) вышел с сервера и понёс наказание 💀'
  leave-message-name-not-resolved:
  - '{discord_mention} вышел с сервера и понёс наказание 💀'
  leave-direct-message:
  - Вы вышли с сервера и были удалены из вайт-листа
  guild-ban-reason: Забанен DEW
  report-message: |-
    {discord_mention} вышел с сервера!
    {report}
  report-warning-not-linked: Аккаунт не был привязан к какому-либо нику
  report-warning-name-not-resolved: Игрок ни разу не заходил на сервер, нельзя вычислить
    ник
  report-unlinked-name-not-resolved: Аккаунт был отвязан от UUID `{uuid}`
  report-unlinked: Аккаунт был отвязан от `{nickname}`
  report-removed-from-whitelist: '`{nickname}` был удалён из вайт-листа'
  report-warning-not-in-whitelist: '`{nickname}` и так не присутствовал в вайт-лист'
  kick-message: <red>Вы были удалены из вайт-листа
  report-banned-from-guild: '{discord_mention} был забанен на сервере'
  report-warning-already-banned: '{discord_mention} уже забанен на сервере'
  report-message-success: Сообщение о выходе было отправлено в <#{channel_id}>
  report-direct-message-success: '{discord_mention} отправлено личное сообщение'
  report-warning-direct-messages-failure: У {discord_mention} закрыты личные сообщения,
    сообщение не отправлено
status:
  disabled: Этот функционал выключен
  status-message-no-players: '## Игроков онлайн нет'
  status-message: |-
    ## {players_count} игроков онлайн:
    {players}
  status-entry: '* {nickname}'
  status-entry-linked: '* {nickname} ({discord_mention})'
```
