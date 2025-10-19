package me.animepdf.dew.util;

import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class BukkitUtils {
    public static UUID uuidFromUsername(@NotNull String username) {
        return Bukkit.getOnlineMode() ? Bukkit.getOfflinePlayer(username).getUniqueId() : crackedUUIDFromUsername(username);
    }

    public static UUID crackedUUIDFromUsername(@NotNull String username) {
        return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
    }

    public static @Nullable String getNickname(@Nullable UUID uuid) {
        if (uuid == null)
            return null;
        String nickname = Bukkit.getOfflinePlayer(uuid).getName();
        if (!Bukkit.getOfflinePlayer(uuid).hasPlayedBefore() || nickname == null)
            return null;
        return nickname;
    }
    public static @NotNull String getNickname(@Nullable UUID uuid, @NotNull String fallback) {
        return Optional.ofNullable(getNickname(uuid)).orElse(fallback);
    }
}
