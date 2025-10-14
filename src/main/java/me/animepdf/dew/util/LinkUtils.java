package me.animepdf.dew.util;

import github.scarsz.discordsrv.DiscordSRV;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.logging.Logger;

public class LinkUtils {
    public static void linkAccount(@NotNull Logger logger, @NotNull UUID uuid, @NotNull String discordId) {
        var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();

        if (linkManager.getUuid(discordId) != null || linkManager.getDiscordId(uuid) != null)
            return;

        linkManager.link(discordId, uuid);
        logger.info(String.format("'%s'(%s) was linked to the %s", Bukkit.getOfflinePlayer(uuid).getName(), uuid, discordId));
    }

    public static void unlinkAccount(@NotNull Logger logger, @NotNull UUID uuid) {
        var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();

        var discordId = linkManager.getDiscordId(uuid);
        if (discordId == null)
            return;

        linkManager.unlink(uuid);
        logger.info(String.format("'%s'(%s) was unlinked from the %s", Bukkit.getOfflinePlayer(uuid).getName(), uuid, discordId));
    }

    public static void unlinkAccount(@NotNull Logger logger, @NotNull String discordId) {
        var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();

        var uuid = linkManager.getUuid(discordId);
        if (uuid == null)
            return;

        linkManager.unlink(discordId);
        logger.info(String.format("'%s'(%s) was unlinked from the %s", Bukkit.getOfflinePlayer(uuid).getName(), uuid, discordId));
    }
}
