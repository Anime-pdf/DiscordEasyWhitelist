package me.animepdf.dew.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class LanguageConfig {

    public String errorPrefix = "\uD83D\uDEAB ";
    public String warningPrefix = "⚠\uFE0F ";
    public String successPrefix = "✅ ";

    @ConfigSerializable
    public static class GeneralMessages {
        public String unknownError = "Неизвестная ошибка. Проверьте привязку используемых ников/аккаунтов";
        public String noPermission = "У вас нет прав для использования этой команды";
        public String wrongCommandArgument = "Аргумент `{arg}` введен некорректно";
        public String noCommandArgument = "Введите хотя бы один аргумент";
        public String tooManyCommandArguments = "Слишком много аргументов";
        public String onlyOnServer = "Эта команда может быть использована только на сервере";
        public String channelNotFound = "Канал `{channel_id}` не найден, сообщение не будет отправлено";

        public GeneralMessages() {
        }
    }
    public GeneralMessages general = new GeneralMessages();

    @ConfigSerializable
    public static class AcceptMessages {
        public String linkWarningAlreadyOthers = "Ник или аккаунт уже привязаны к кому-то другому, сначала отвяжите их";
        public String linkWarningAlreadySame = "{discord_mention} уже привязан к `{username}`";
        public String linkSuccess = "{discord_mention} был привязан к `{username}`";

        public String whitelistWarningAlready = "Ник `{username}` уже присутствует в вайт-листе";
        public String whitelistSuccess = "Ник `{username}` добавлен в вайт-лист";

        public String guildNameSuccess = "Имя {discord_mention} было изменено на `{username}`";

        public String guildRoleRemoveSuccess = "Роли {roles} были удалены";
        public String guildRoleAddSuccess = "Роли {roles} были добавлены";

        public List<String> welcomeMessage = List.of(
                "{discord_mention}, добро пожаловать!",
                "Тебя добавили на сервер. IP находится в <#1086714411848310934>, либо с помощью команды `!ip`. Версия 1.21.8.",
                "Рекомендую также посмотреть канал <#1285560328490324008>, чтобы начать ориентироваться на сервере. Приятной игры!",
                "",
                "*Анкета рассмотрена {moderator_mention}*"
        );
        public String welcomeMessageSuccess = "В <#{channel_id}> отправлено приветственное сообщение";

        public String acceptSuccess = "Отчёт:\n{report}";

        public AcceptMessages() {
        }
    }
    public AcceptMessages accept = new AcceptMessages();

    @ConfigSerializable
    public static class RemoveMessages {
        public String linkErrorOther = "Ник или аккаунт уже привязаны к кому-то другому, сначала отвяжите их";
        public String linkErrorNeverPlayed = "{discord_mention} никогда не заходил на сервер, невозможно получить ник, укажите его вручную";
        public String linkErrorNotLinked = "{discord_mention} не привязан к какому-либо нику";
        public String linkWarningNotLinked = "{discord_mention} не привязан к указанному нику";
        public String linkWarningNotLinkedFallback = "{discord_mention} не привязан к какому-либо нику. Использую `{fallback}`";
        public String linkSuccess = "{discord_mention} был отвязан от `{username}`";

        public String whitelistWarningAlready = "Ник `{username}` и так не присутствует в вайт-листе";
        public String whitelistSuccess = "Ник `{username}` удалён из вайт-листа";

        public Component kickMessage = Component.text("Вы были удалены из вайт-листа", NamedTextColor.RED);

        public List<String> removeMessage = List.of(
                "{discord_mention} (`{username}`) был забанен"
        );
        public List<String> removeMessageReason = List.of(
                "{discord_mention} (`{username}`) был забанен по причине: \"{reason}\""
        );
        public String removeMessageSuccess = "В <#{channel_id}> отправлено сообщение о бане";

        public List<String> removeDirectMessage = List.of(
                "Вы были забанены на сервере"
        );
        public List<String> removeDirectMessageReason = List.of(
                "Вы были забанены на сервере по причине: \"{reason}\""
        );
        public String removeDirectMessageSuccess = "{discord_mention} отправлено личное сообщение";
        public String removeDirectMessageFailure = "У {discord_mention} закрыты личные сообщения, сообщение не отправлено";

        public String guildBanReason = "Забанен DEW";
        public String guildBanSuccess = "{discord_mention} забанен на сервере";

        public String removeSuccess = "Отчёт:\n{report}";

        public RemoveMessages() {
        }
    }
    public RemoveMessages remove = new RemoveMessages();

    @ConfigSerializable
    public static class WhitelistMessages {
        public String containsUsername = "Ник `{username}` присутствует в вайт-листе";
        public String notContainsUsername = "Ник `{username}` не присутствует в вайт-листе";

        public String addedUsername = "Ник `{username}` добавлен в вайт-лист";
        public String notAddedUsername = "Ник `{username}` уже есть в вайт-листе";

        public String removedUsername = "Ник `{username}` удалён из вайт-листа";
        public String notRemovedUsername = "Ник `{username}` и так не присутствует в вайт-листе";

        public WhitelistMessages() {
        }
    }
    public WhitelistMessages whitelist = new WhitelistMessages();

    @ConfigSerializable
    public static class LinkMessages {
        public String disabled = "Этот функционал выключен";

        public String nicknameNotLinked = "`{username}` не привязан к какому-либо аккаунту";
        public String nicknameLinkedOnServer = "`{username}` привязан к {discord_mention}";
        public String nicknameLinkedNotOnServer = "`{username}` привязан к {discord_mention}, однако он не на сервере";

        public String userNotLinked = "{discord_mention} не привязан к какому-либо нику";
        public String userLinked = "{discord_mention} привязан к `{username}`";
        public String userLinkedNoNickname = "{discord_mention} привязан к UUID `{uuid}`, игрок ни разу не заходил на сервер, поэтому вычислить ник невозможно";

        public String linkWarningAlreadyOthers = "Ник или аккаунт уже привязаны к кому-то другому, сначала отвяжите их";
        public String linkWarningAlreadySame = "{discord_mention} уже привязан к `{username}`";
        public String linkSuccess = "{discord_mention} был привязан к `{username}`";

        public String unlinkNicknameNotFound = "`{username}` не привязан к какому-либо аккаунту";
        public String unlinkNicknameSuccess = "`{username}` был отвязан от {discord_mention}";

        public String unlinkUserNotFound = "{discord_mention} не привязан к какому-либо нику";
        public String unlinkUserSuccessNoNickname = "{discord_mention} был отвязан от UUID `{uuid}`, игрок ни разу не заходил на сервер, поэтому вычислить ник невозможно";
        public String unlinkUserSuccess = "{discord_mention} был отвязан от `{username}`";

        public LinkMessages() {
        }
    }
    public LinkMessages link = new LinkMessages();

    @ConfigSerializable
    public static class ReloadMessages {
        public String reloaded = "Конфиг перезагружен";

        public ReloadMessages() {
        }
    }
    public ReloadMessages reload = new ReloadMessages();

    @ConfigSerializable
    public static class LeaveMessages {
        public List<String> leaveMessage = List.of(
                "{discord_mention} (`{username}`) вышел с сервера и понёс наказание 💀"
        );
        public List<String> leaveMessageNameNotResolved = List.of(
                "{discord_mention} вышел с сервера и понёс наказание 💀"
        );
        public List<String> leaveDirectMessage = List.of(
                "Вы вышли с сервера и были удалены из вайт-листа"
        );

        public String guildBanReason = "Забанен DEW";

        public String reportMessage = "{discord_mention} вышел с сервера!\n{report}";

        public String reportWarningNotLinked = "Аккаунт не был привязан к какому-либо нику";
        public String reportWarningNameNotResolved = "Игрок ни разу не заходил на сервер, нельзя вычислить ник";
        public String reportUnlinkedNameNotResolved = "Аккаунт был отвязан от UUID `{uuid}`";
        public String reportUnlinked = "Аккаунт был отвязан от `{username}`";

        public String reportRemovedFromWhitelist = "`{username}` был удалён из вайт-листа";
        public String reportWarningNotInWhitelist = "`{username}` и так не присутствовал в вайт-лист";

        public Component kickMessage = Component.text("Вы были удалены из вайт-листа", NamedTextColor.RED);

        public String reportBannedFromGuild = "{discord_mention} был забанен на сервере";
        public String reportWarningAlreadyBanned = "{discord_mention} уже забанен на сервере";

        public String reportMessageSuccess = "Сообщение о выходе было отправлено в <#{channel_id}>";

        public String reportDirectMessageSuccess = "{discord_mention} отправлено личное сообщение";
        public String reportWarningDirectMessagesFailure = "У {discord_mention} закрыты личные сообщения, сообщение не отправлено";

        public LeaveMessages() {
        }
    }
    public LeaveMessages leave = new LeaveMessages();

    @ConfigSerializable
    public static class StatusMessages {
        public String disabled = "Этот функционал выключен";

        public String statusMessageNoPlayers = "## Игроков онлайн нет";
        public String statusMessage = "## {players_count} игроков онлайн:\n{players}";
        public String statusEntry = "* {username}";
        public String statusEntryLinked = "* {username} ({discord_mention})";

        public StatusMessages() {
        }
    }
    public StatusMessages status = new StatusMessages();

    public LanguageConfig() {
    }

    public static LanguageConfig createRussian() {
        return new LanguageConfig();
    }

    public static LanguageConfig createEnglish() {
        LanguageConfig config = new LanguageConfig();

        config.errorPrefix = "🚫 ";
        config.warningPrefix = "⚠️ ";
        config.successPrefix = "✅ ";

        // General
        config.general.unknownError = "Unknown error. Check linking status of nickname/user being used";
        config.general.noPermission = "You don't have permission to use this command";
        config.general.wrongCommandArgument = "Invalid argument: `{arg}`";
        config.general.noCommandArgument = "Enter at least one argument";
        config.general.tooManyCommandArguments = "Too many arguments entered";
        config.general.onlyOnServer = "This command can only be used on discord server";
        config.general.channelNotFound = "Channel `{channel_id}` not found, message won't be sent";

        // Accept
        config.accept.linkWarningAlreadyOthers = "Nickname or user are already linked to someone else, try unlinking them first";
        config.accept.linkWarningAlreadySame = "{discord_mention} already linked to `{username}`";
        config.accept.linkSuccess = "{discord_mention} was linked to `{username}`";
        config.accept.whitelistWarningAlready = "Nickname `{username}` already persists in the whitelist";
        config.accept.whitelistSuccess = "Nickname `{username}` was added to the whitelist";
        config.accept.guildNameSuccess = "{discord_mention}'s guild name was changed to `{username}`";
        config.accept.guildRoleRemoveSuccess = "Roles {roles} were removed";
        config.accept.guildRoleAddSuccess = "Roles {roles} were added";
        config.accept.welcomeMessage = List.of(
                "{discord_mention} Welcome!",
                "You was added to the server, IP can be found in <#0000000000000000000>, or using command `!ip`. Minecraft version 1.21.8.",
                "Yoy should also check out <#0000000000000000000>, it contains useful information to start playing on the server comfortably. Have fun!",
                "",
                "*Application review by {moderator_mention}*"
        );
        config.accept.welcomeMessageSuccess = "Welcome message was sent into <#{channel_id}>";
        config.accept.acceptSuccess = "Logs:\n{report}";

        // Remove
        config.remove.linkErrorOther = "Nickname or user are already linked to someone else, try unlinking them first";
        config.remove.linkErrorNeverPlayed = "{discord_mention} never entered the server, can't resolve their nickname, enter it manually";
        config.remove.linkErrorNotLinked = "{discord_mention} isn't linked to any nickname";
        config.remove.linkWarningNotLinked = "{discord_mention} isn't linked to entered nickname";
        config.remove.linkWarningNotLinkedFallback = "{discord_mention} isn't linked to any nickname, falling back to `{fallback}`";
        config.remove.linkSuccess = "{discord_mention} was unlinked from `{username}`";
        config.remove.whitelistWarningAlready = "Whitelist already doesn't contain `{username}`";
        config.remove.whitelistSuccess = "Nickname `{username}` was removed from the whitelist";
        config.remove.kickMessage = Component.text("You was removed from the whitelist", NamedTextColor.RED);
        config.remove.removeMessage = List.of("{discord_mention} (`{username}`) was banned");
        config.remove.removeMessageReason = List.of("{discord_mention} (`{username}`) was banned with the reason: \"{reason}\"");
        config.remove.removeMessageSuccess = "Remove message was sent into <#{channel_id}>";
        config.remove.removeDirectMessage = List.of("You was banned on the server");
        config.remove.removeDirectMessageReason = List.of("You was banned on the server with a reason: \"{reason}\"");
        config.remove.removeDirectMessageSuccess = "Direct message was sent to {discord_mention}";
        config.remove.removeDirectMessageFailure = "{discord_mention} has closed DMs, message not sent";
        config.remove.guildBanReason = "Banned by DEW";
        config.remove.guildBanSuccess = "{discord_mention} was banned on the server";
        config.remove.removeSuccess = "Logs:\n{report}";

        // Whitelist
        config.whitelist.containsUsername = "Whitelist contains `{username}`";
        config.whitelist.notContainsUsername = "Whitelist doesn't contain `{username}`";
        config.whitelist.addedUsername = "Username `{username}` was added to the whitelist";
        config.whitelist.notAddedUsername = "Whitelist already contains `{username}`";
        config.whitelist.removedUsername = "Username `{username}` was removed from the whitelist";
        config.whitelist.notRemovedUsername = "Whitelist already doesn't contain `{username}`";

        // Link
        config.link.disabled = "This functionality is disabled";
        config.link.nicknameNotLinked = "`{username}` isn't linked to any user";
        config.link.nicknameLinkedOnServer = "`{username}` is linked to {discord_mention}";
        config.link.nicknameLinkedNotOnServer = "`{username}` is linked to {discord_mention}, but he's not on the server";
        config.link.userNotLinked = "{discord_mention} isn't linked to any nickname";
        config.link.userLinked = "{discord_mention} is linked to `{username}`";
        config.link.userLinkedNoNickname = "{discord_mention}  is linked to UUID `{uuid}`, player didn't join server once, can't retrieve his nickname";
        config.link.linkWarningAlreadyOthers = "Nickname or user are already linked to someone else, try unlinking them first";
        config.link.linkWarningAlreadySame = "{discord_mention} already linked to `{username}`";
        config.link.linkSuccess = "{discord_mention} was linked to `{username}`";
        config.link.unlinkNicknameNotFound = "`{username}` isn't linked to any user";
        config.link.unlinkNicknameSuccess = "`{username}` was unlinked from {discord_mention}";
        config.link.unlinkUserNotFound = "{discord_mention} isn't linked to any nickname";
        config.link.unlinkUserSuccessNoNickname = "{discord_mention} was unlinked from UUID `{uuid}`, player didn't join server once, can't retrieve his nickname";
        config.link.unlinkUserSuccess = "{discord_mention} was unlinked from `{username}`";

        // Reload
        config.reload.reloaded = "Config reloaded";

        // Leave
        config.leave.leaveMessage = List.of("{discord_mention} (`{username}`) left the server and faced consequences 💀");
        config.leave.leaveMessageNameNotResolved = List.of("{discord_mention} left the server and faced consequences 💀");
        config.leave.leaveDirectMessage = List.of("You left the server and was removed from the whitelist");
        config.leave.guildBanReason = "Banned by DEW";
        config.leave.reportMessage = "{discord_mention} left the server!\n{report}";
        config.leave.reportWarningNotLinked = "User wasn't linked to any nickname";
        config.leave.reportWarningNameNotResolved = "Player didn't join server once, can't retrieve his nickname";
        config.leave.reportUnlinkedNameNotResolved = "User was unlinked from UUID `{uuid}`";
        config.leave.reportUnlinked = "User was unlinked from `{username}`";
        config.leave.reportRemovedFromWhitelist = "`{username}` was removed from the whitelist";
        config.leave.reportWarningNotInWhitelist = "Whitelist already didn't contain `{username}`";
        config.leave.kickMessage = Component.text("You was removed from the whitelist", NamedTextColor.RED);
        config.leave.reportBannedFromGuild = "{discord_mention} was banned from the server";
        config.leave.reportWarningAlreadyBanned = "{discord_mention} was already banned from the server";
        config.leave.reportMessageSuccess = "Leave message was sent into <#{channel_id}>";
        config.leave.reportDirectMessageSuccess = "Direct message was sent to {discord_mention}";
        config.leave.reportWarningDirectMessagesFailure = "{discord_mention} has closed DMs, message not sent";

        // Status
        config.status.disabled = "This functionality is disabled";
        config.status.statusMessageNoPlayers = "## No players online";
        config.status.statusMessage = "## {players_count} players online:\n{players}";
        config.status.statusEntry = "* {username}";
        config.status.statusEntryLinked = "* {username} ({discord_mention})";

        return config;
    }
}
