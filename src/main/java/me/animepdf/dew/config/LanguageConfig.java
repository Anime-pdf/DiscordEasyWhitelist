package me.animepdf.dew.config;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class LanguageConfig {

    // Prefix
    public String errorPrefix = "\uD83D\uDEAB ";
    public String warningPrefix = "⚠\uFE0F ";
    public String successPrefix = "✅ ";

    // General
    public String noPermission = "Ошибка. У вас нет прав для использования этой команды";
    public String wrongCommandArgument = "Ошибка. {arg} введен некорректно";
    public String noCommandArgument = "Ошибка. Введите хотя бы один аргумент";
    public String tooManyCommandArguments = "Ошибка. Слишком много аргументов";
    public String outputWithWarnings = "Предупреждения: \n{output}\n\n";
    public String channelNotFound = "Канал `{channel_id}` не найден, сообщение не будет отправлено";

    // Accept
    public String acceptOutputWithoutWarnings = "{discord_mention} успешно добавлен и привязан к нику `{username}`!";
    public List<String> welcomeMessage = List.of(
            "{discord_mention}, добро пожаловать!",
            "Тебя добавили на сервер. IP находится в <#1086714411848310934>, либо с помощью команды `!ip`. Версия 1.21.8.",
            "Рекомендую также посмотреть канал <#1285560328490324008>, чтобы начать ориентироваться на сервере. Приятной игры!"
    );

    // Remove
    public Component kickMessage = Component.text("Вы были удалены из вайт-листа", NamedTextColor.RED);
    public String removeOutputWithoutWarnings = "{discord_mention} (`{username}`) успешно забанен и удалён из вайт-листа!";
    public String discordBanReason = "Забанен DEW";
    public List<String> banMessage = List.of(
            "{discord_mention} (`{discord_name}`) был забанен."
    );

    // Whitelist
    public String whitelistAlreadyContainsUsername = "Ник `{username}` уже присутствует в вайт-листе";
    public String whitelistContainsUsername = "Ник `{username}` присутствует в вайт-листе";
    public String whitelistNotContainsUsername = "Ник `{username}` не присутствует в вайт-листе";
    public String whitelistAddedUsername = "Ник `{username}` добавлен в вайт-лист";
    public String whitelistRemovedUsername = "Ник `{username}` удалён из вайт-листа";

    // Link
    public String linkingDisabled = "Этот функционал выключен";
    public String discordAlreadyLinked = "{discord_mention} уже привязан к нику `{username}`";
    public String usernameLinked = "`{username}` привязан к {discord_mention}";
    public String usernameNotLinked = "`{username}` не привязан к какому-либо аккаунту";
    public String usernameUnLinked = "`{username}` был отвязан от аккаунта";
    public String discordLinked = "{discord_mention} привязан к нику `{username}`";
    public String discordNotLinked = "{discord_mention} не привязан к какому-либо нику";
    public String discordUnLinked = "{discord_mention} был отвязан от ника";
    public String discordOrUsernameAlreadyLinked = "Ник или аккаунт уже привязаны к кому-то другому, сначала отвяжите их";

    // Reload
    public String configReloaded = "Конфиг перезагружен";

    public LanguageConfig() {
    }
}
