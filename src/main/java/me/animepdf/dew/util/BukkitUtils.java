package me.animepdf.dew.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class BukkitUtils {
    public static UUID uuidFromUsername(@NotNull String username) {
        return Bukkit.getOnlineMode() ? Bukkit.getOfflinePlayer(username).getUniqueId() : crackedUUIDFromUsername(username);
    }

    public static UUID crackedUUIDFromUsername(@NotNull String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }
}
