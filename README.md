# DiscordEasyWhitelist

A powerful and easy-to-use Minecraft (Paper/Spigot) plugin that integrates server whitelisting and account management directly into Discord via DiscordSRV slash commands.

## ✨ Features

**DiscordEasyWhitelist** streamlines the process of managing your server's whitelist by utilizing Discord slash commands for a seamless administrative experience.

### Core Capabilities:

* **Discord-Native Whitelisting (`/accept`)**: Easily add new players to the server whitelist with a simple Discord command, eliminating the need for in-game console commands.
* **Whitelisting Management (`/whitelist`)**: Direct control over the whitelist with subcommands to add, remove, and check player status.
* **Automated Account Linking**: Optionally link a player's Minecraft username to their Discord account upon whitelisting, which is managed by the required **SimpleWhitelist** plugin.
* **Automated Role Management**: Automatically assign new roles and remove old roles from the Discord member upon successful whitelisting (e.g., removing the "Applicant" role and adding the "Member" role).
* **Player Removal and Ban (`/remove`)**: Quickly remove a player from the whitelist and optionally ban the corresponding Discord member. The player will be kicked from the server upon removal.
* **Configuration Reload**: In-place configuration reloading via a dedicated slash command (`/reload`) without needing a server restart.
* **Customizable Messaging**: Full control over all messages, including error, warning, and success prefixes, and channel-specific welcome/ban notifications.
* **Permission Control**: Commands are restricted to users with specified Discord role IDs (moderator roles).
* **Dedicated Channels**: Configurable Discord channels for sending welcome and ban notifications.

## 📦 Dependencies

This plugin requires two other plugins to function correctly:

| Dependency | Purpose |
| :--- | :--- |
| **SimpleWhitelist** (`net.aniby.simplewhitelist`) | The backend plugin that handles the core whitelisting and account linking logic. **DiscordEasyWhitelist** acts as a Discord frontend for this plugin. |
| **DiscordSRV** (`github.scarsz.discordsrv`) | Provides the framework for registering and handling Discord slash commands. |

*Note: Ensure your server platform is Paper or Spigot (or a fork like Purpur) and that both dependencies are installed and running before installing this plugin.*

## ⚙️ Installation

1.  Download the latest release of **DiscordEasyWhitelist**.
2.  Ensure you have **SimpleWhitelist** and **DiscordSRV** installed in your server's `/plugins` folder.
3.  Place the **DiscordEasyWhitelist** jar file into your server's `/plugins` folder.
4.  Start (or restart) your server.
5.  Configure the generated `config.yml` and `language.yml` files in the `/plugins/DiscordEasyWhitelist` folder.
6.  Use the `/reload` slash command in Discord or restart the server to apply the configuration changes.

## 📝 Configuration

Configuration is split into two files: `config.yml` for general settings and IDs, and `language.yml` for all user-facing messages.

### `config.yml` (General Settings)

This file manages operational settings and Discord entity IDs.

| Option | Type           | Description                                                                                                                                 | Placeholder |
| :--- |:---------------|:--------------------------------------------------------------------------------------------------------------------------------------------| :--- |
| `enable-linking` | boolean        | Enables/Disables the automatic linking functionality.                                                                                       | `enableLinking` |
| `moderator-role-ids` | List\<String\> | A list of Discord Role IDs that are authorized to use administrative commands like `/accept`, `/remove`, and `/reload`.                     | `moderatorRoleIds` |
| `roles-to-remove` | List\<String\> | A list of Discord Role IDs that will be **removed** from a member when they are successfully whitelisted (e.g., removing a "Waiting" role). | `rolesToRemove` |
| `roles-to-add` | List\<String\> | A list of Discord Role IDs that will be **added** to a member when they are successfully whitelisted (e.g., adding a "Member" role).        | `rolesToAdd` |
| `welcome-channel-id` | String         | The Discord Channel ID where the `welcomeMessage` will be sent after a successful `/accept`.                                                | `welcomeChannelId` |
| `bans-channel-id` | String         | The Discord Channel ID where the `banMessage` will be sent after a successful `/remove`.                                                    | `bansChannelId` |

-----

### `language.yml` (Message Customization)

This file contains all messages sent by the plugin. It supports Kyori Adventure `Component` formatting for Minecraft messages (only `kickMessage` for now) and a custom `MessageFormatter` for Discord messages, which utilizes various placeholders.
Messages are in **Russian** by default, but can be fully customized.

| Option | Type | Default Value | Purpose | Placeholders Used |
| :--- | :--- | :--- | :--- | :--- |
| **Prefix** | | | | |
| `errorPrefix` | String | `🚫 ` | Prefix for error messages. | N/A |
| `warningPrefix` | String | `⚠️ ` | Prefix for warning messages. | N/A |
| `successPrefix` | String | `✅ ` | Prefix for success messages. | N/A |
| **General** | | | | |
| `noPermission` | String | `Ошибка. У вас нет прав для использования этой команды` | Message when a user lacks command permission. | N/A |
| `wrongCommandArgument` | String | `Ошибка. {arg} введен некорректно` | Message for an invalid command argument. | `{arg}` |
| `noCommandArgument` | String | `Ошибка. Введите хотя бы один аргумент` | Message when a required argument is missing. | N/A |
| `tooManyCommandArguments` | String | `Ошибка. Слишком много аргументов` | Message when too many arguments are provided. | N/A |
| `outputWithWarnings` | String | `Предупреждения: \n{output}\n\n` | Wrapper for output that includes warnings. | `{output}` |
| `channelNotFound` | String | `Канал \`{channel_id}\` не найден, сообщение не будет отправлено` | Warning when a configured Discord channel ID is invalid. | `{channel_id}` |
| **Accept** | | | | |
| `acceptOutputWithoutWarnings` | String | `{discord_mention} успешно добавлен и привязан к нику \`{username}\`!` | Success message for the `/accept` command. | `{discord_mention}`, `{username}` |
| `welcomeMessage` | List<String> | List of custom welcome messages. | Messages sent to the `welcomeChannelId` after `/accept`. | `{discord_mention}` |
| **Remove** | | | | |
| `kickMessage` | Component | `Вы были удалены из вайт-листа` (Red Text) | The message displayed when a player is kicked from the server (e.g., via `/remove`). | N/A |
| `removeOutputWithoutWarnings` | String | `{discord_mention} (\`{username}\`) успешно забанен и удалён из вайт-листа!` | Success message for the `/remove` command. | `{discord_mention}`, `{username}` |
| `discordBanReason` | String | `Забанен DEW` | The reason recorded in the Discord Audit Log when a user is banned via `/remove`. | N/A |
| `banMessage` | List<String> | List of custom ban messages. | Messages sent to the `bansChannelId` after `/remove`. | `{discord_mention}`, `{discord_name}` |
| **Whitelist** | | | | |
| `whitelistAlreadyContainsUsername` | String | `Ник \`{username}\` уже присутствует в вайт-листе` | Message for `/whitelist add` when the player is already whitelisted. | `{username}` |
| `whitelistContainsUsername` | String | `Ник \`{username}\` присутствует в вайт-листе` | Message for `/whitelist check` when the player is whitelisted. | `{username}` |
| `whitelistNotContainsUsername` | String | `Ник \`{username}\` не присутствует в вайт-листе` | Message for `/whitelist check` when the player is not whitelisted. | `{username}` |
| `whitelistAddedUsername` | String | `Ник \`{username}\` добавлен в вайт-лист` | Success message for `/whitelist add`. | `{username}` |
| `whitelistRemovedUsername` | String | `Ник \`{username}\` удалён из вайт-листа` | Success message for `/whitelist remove`. | `{username}` |
| **Link** | | | | |
| `linkingDisabled` | String | `Этот функционал выключен` | Error message when a link command is used but linking is disabled. | N/A |
| `discordAlreadyLinked` | String | `{discord_mention} уже привязан к нику \`{username}\`` | Message when the Discord member is already linked. | `{discord_mention}`, `{username}` |
| `usernameLinked` | String | `\`{username}\` привязан к {discord_mention}` | Output showing the link status for a username check. | `{username}`, `{discord_mention}` |
| `usernameNotLinked` | String | `\`{username}\` не привязан к какому-либо аккаунту` | Output when a username is not linked to any Discord account. | `{username}` |
| `usernameUnLinked` | String | `\`{username}\` был отвязан от аккаунта` | Success message for unlinking a username. | `{username}` |
| `discordLinked` | String | `{discord_mention} привязан к нику \`{username}\`` | Output showing the link status for a member check. | `{discord_mention}`, `{username}` |
| `discordNotLinked` | String | `{discord_mention} не привязан к какому-либо нику` | Output when a Discord member is not linked to any username. | `{discord_mention}` |
| `discordUnLinked` | String | `{discord_mention} был отвязан от ника` | Success message for unlinking a Discord member. | `{discord_mention}` |
| `discordOrUsernameAlreadyLinked` | String | `Ник или аккаунт уже привязаны к кому-то другому, сначала отвяжите их` | Error when trying to link an already linked entity. | N/A |
| **Reload** | | | | |
| `configReloaded` | String | `Конфиг перезагружен` | Success message for the `/reload` command. | N/A |

## 🕹️ Commands and Usage

All commands are implemented as Discord Slash Commands. They will appear in your server's Discord channels once DiscordSRV has synced them.

### Administrator Commands (Requires a role in `moderatorRoleIds`)

| Command | Usage | Description |
| :--- | :--- | :--- |
| `/accept <member> <username>` | Whitelists a Discord member's Minecraft username. Performs role changes, sends welcome messages, and optionally links the accounts. |
| `/remove <member> <username>` | Removes a Minecraft username from the whitelist, kicks the player, and optionally bans the Discord member. Sends a ban notification. |
| `/reload` | Reloads the `config.yml` and `language.yml` files. |

### Whitelist Commands

| Command | Usage | Description |
| :--- | :--- | :--- |
| `/whitelist add <username>` | Manually adds a Minecraft username to the server whitelist. |
| `/whitelist remove <username>` | Manually removes a Minecraft username from the server whitelist and kicks the player if they are online. |
| `/whitelist check <username>` | Checks if a Minecraft username is currently on the server whitelist. |

### Linking Commands (Only available if `enableLinking: true`)

| Command | Usage | Description |
| :--- | :--- | :--- |
| `/link <username>` | Links the command sender's Discord account to the specified Minecraft username. |
| `/link unlink username <username>` | Unlinks the specified Minecraft username from any linked Discord account. |
| `/link unlink member` | Unlinks the command sender's Discord account from its linked Minecraft username. |
| `/link check username <username>` | Checks which Discord account is linked to the Minecraft username. |
| `/link check member` | Checks which Minecraft username is linked to the command sender's Discord account. |

## 🌟 Placeholders

The `language.yml` file supports a comprehensive set of placeholders for dynamic messaging:

| Placeholder | Context | Description |
| :--- | :--- | :--- |
| `{username}` | All commands | The target Minecraft username. |
| `{discord_mention}` | `/accept`, `/remove`, `/link` | The target Discord member's mention (e.g., `@user#1234` or `@user`). |
| `{discord_username}` | `/accept`, `/remove`, `/link` | The target Discord member's full tag (e.g., `user#1234`). |
| `{discord_name}` | `/accept`, `/remove`, `/link` | The target Discord member's effective display name (e.g., nickname or username). |
| `{discord_id}` | `/accept`, `/remove`, `/link` | The target Discord member's ID. |
| `{output}` | All commands (with warnings) | The detailed warning messages, joined by newlines. |
| `{command}` | Error messages | The command path (e.g., `whitelist/remove`). |
| `{arg}` | Error messages | The name of the wrong/missing command argument. |
| `{channel_id}` | Error messages | The ID of a channel that could not be found. |
