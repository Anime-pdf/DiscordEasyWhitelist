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

        public String discordNameSuccess = "Имя {discord_mention} было изменено на `{username}`";

        public String discordRoleRemoveSuccess = "Роли {roles} были удалены";
        public String discordRoleAddSuccess = "Роли {roles} были добавлены";

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

        public List<String> banMessage = List.of(
                "{discord_mention} (`{username}`) был забанен"
        );
        public List<String> banMessageReason = List.of(
                "{discord_mention} (`{username}`) был забанен по причине: \"{reason}\""
        );
        public String banMessageSuccess = "В <#{channel_id}> отправлено сообщение о бане";


        public List<String> banDirectMessage = List.of(
                "Вы были забанены на сервере"
        );
        public List<String> banDirectMessageReason = List.of(
                "Вы были забанены на сервере по причине: \"{reason}\""
        );
        public String banDirectMessageSuccess = "{discord_mention} отправлено сообщение о бане";
        public String banDirectMessageFailure = "У {discord_mention} закрыты личные сообщения";

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

    public LanguageConfig() {
    }
}
