package me.animepdf.dew.util;

import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import me.animepdf.dew.config.ConfigContainer;
import org.jetbrains.annotations.NotNull;

public class DiscordUtils {
    public static boolean hasModPermission(@NotNull ConfigContainer configContainer, @NotNull Member member) {
        boolean found = false;
        for (Role role : member.getRoles()) {
            if (configContainer.getGeneralConfig().moderatorRoleIds.contains(role.getId())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRole(@NotNull Member member, @NotNull String roleId) {
        boolean found = false;
        for (Role role : member.getRoles()) {
            if (role.getId().equals(roleId)) {
                return true;
            }
        }
        return false;
    }
}
