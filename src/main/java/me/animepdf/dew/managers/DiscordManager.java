package me.animepdf.dew.managers;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Role;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.config.ConfigContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DiscordManager {
    private final DiscordEasyWhitelist plugin;
    private final ConfigContainer configContainer;

    public DiscordManager(@NotNull DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = this.plugin.getConfigContainer();
    }

    public @Nullable User getUser(@Nullable String discordId) {
        if (discordId == null)
            return null;
        return DiscordSRV.getPlugin().getJda().getUserById(discordId);
    }

    public void changeName(@NotNull Member member, @NotNull String username) {
        member.modifyNickname(username).queue();
    }
    public void banMember(@NotNull Member member, @NotNull String reason) {
        member.getGuild().ban(member, 0, reason).queue();
    }
    public void banUser(@NotNull Guild guild, @NotNull User user, @NotNull String reason) {
        guild.ban(user, 0, reason).queue();
    }

    public boolean hasModPermission(@NotNull Member member) {
        for (Role role : member.getRoles()) {
            if (this.configContainer.getGeneralConfig().moderatorRoles.contains(role.getId())) {
                return true;
            }
        }
        return false;
    }
    public static boolean hasRole(@NotNull Member member, @NotNull String roleId) {
        for (Role role : member.getRoles()) {
            if (role.getId().equals(roleId)) {
                return true;
            }
        }
        return false;
    }

    public void send(@NotNull SlashCommandEvent event, @NotNull String message) {
        event.getHook().sendMessage(
                        message
        ).queue();
    }
    public void sendError(@NotNull SlashCommandEvent event, @NotNull String message) {
        event.getHook().sendMessage(
                this.configContainer.getLanguageConfig().errorPrefix +
                        message
        ).queue();
    }
    public void sendWarning(@NotNull SlashCommandEvent event, @NotNull String message) {
        event.getHook().sendMessage(
                this.configContainer.getLanguageConfig().warningPrefix +
                        message
        ).queue();
    }
    public void sendSuccess(@NotNull SlashCommandEvent event, @NotNull String message) {
        event.getHook().sendMessage(
                this.configContainer.getLanguageConfig().successPrefix +
                        message
        ).queue();
    }

    public String appendError(@NotNull String message) {
        return this.configContainer.getLanguageConfig().errorPrefix + message;
    }
    public String appendWarning(@NotNull String message) {
        return this.configContainer.getLanguageConfig().warningPrefix + message;
    }
    public String appendSuccess(@NotNull String message) {
        return this.configContainer.getLanguageConfig().successPrefix + message;
    }
}
