package me.animepdf.dew.managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.objects.managers.AccountLinkManager;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.util.BukkitUtils;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.logging.Logger;

public class LinkManager {
    private final DiscordEasyWhitelist plugin;
    private final Logger logger;

    public LinkManager(@NotNull DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.logger = this.plugin.getLogger();
    }

    private AccountLinkManager linkManager() {
        return DiscordSRV.getPlugin().getAccountLinkManager();
    }

    public @Nullable String getLinkedDiscord(@NotNull UUID uuid) {
        return linkManager().getDiscordId(uuid);
    }
    public @Nullable String getLinkedDiscord(@NotNull String username) {
        return linkManager().getDiscordId(BukkitUtils.uuidFromNickname(username));
    }
    public @Nullable UUID getLinkedUUID(@NotNull String discordId) {
        return linkManager().getUuid(discordId);
    }

    public boolean linkAccount(@NotNull UUID uuid, @NotNull String discordId) {
        if (linkManager().getUuid(discordId) != null || linkManager().getDiscordId(uuid) != null)
            return true;

        linkManager().link(discordId, uuid);
        logger.info(String.format("%s('%s') was linked to the %s", uuid, Bukkit.getOfflinePlayer(uuid).getName(), discordId));
        return false;
    }

    public boolean unlinkAccount(@NotNull UUID uuid) {
        var discordId = linkManager().getDiscordId(uuid);
        if (discordId == null)
            return true;

        linkManager().unlink(uuid);
        logger.info(String.format("%s('%s') was unlinked from the %s", uuid, Bukkit.getOfflinePlayer(uuid).getName(), discordId));
        return false;
    }

    public boolean unlinkAccount(@NotNull String discordId) {
        var uuid = linkManager().getUuid(discordId);
        if (uuid == null)
            return true;

        linkManager().unlink(discordId);
        logger.info(String.format("%s('%s') was unlinked from the %s", uuid, Bukkit.getOfflinePlayer(uuid).getName(), discordId));
        return false;
    }
}
