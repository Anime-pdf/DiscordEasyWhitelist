package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.abstact.DEWComponent;
import me.animepdf.dew.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AcceptCommand extends DEWComponent implements SlashCommandProvider  {
    public AcceptCommand(DiscordEasyWhitelist plugin) {
        super(plugin);
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("accept", "Accept a newbie to the server")
                        .addOption(OptionType.USER, "user", "User to add", true)
                        .addOption(OptionType.STRING, "nickname", "Minecraft nickname to add to whitelist", true)
                )
        );
    }

    @SlashCommand(path = "accept", deferReply = true, deferEphemeral = true)
    public void acceptCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().accept.enable) {
            discordManager.sendError(event,
                    lang().accept.disabled
            );
            return;
        }

        User user;
        String nickname;

        // data integrity
        {
            var userRaw = event.getOption("user");
            var nicknameRaw = event.getOption("nickname");

            if (userRaw == null) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "accept",
                                "arg", "user")
                );
                return;
            }
            user = userRaw.getAsUser();

            if (nicknameRaw == null || nicknameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "accept",
                                "arg", "nickname")
                );
                return;
            }
            nickname = nicknameRaw.getAsString().strip();
        }

        var guild = event.getGuild();
        List<String> output = new ArrayList<>();

        // discord
        if (general().accept.linkToNickname) {
            String targetDiscord = user.getId();
            UUID targetUUID = BukkitUtils.uuidFromNickname(nickname);
            String linkedDiscord = linkManager.getLinkedDiscord(targetUUID);
            UUID linkedUUID = linkManager.getLinkedUUID(targetDiscord);

            if (linkedDiscord == null && linkedUUID == null) {
                if (linkManager.linkAccount(BukkitUtils.uuidFromNickname(nickname), user.getId())) {
                    discordManager.sendError(event, lang().general.unknownError);
                    return;
                } else {
                    output.add(discordManager.appendSuccess(
                            MessageFormatter.format(
                                    lang().accept.linkSuccess,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "uuid", targetUUID,
                                    "nickname", nickname
                            )
                    ));
                }
            } else if (linkedUUID != null && linkedDiscord != null &&
                    linkedUUID.equals(targetUUID) &&
                    linkedDiscord.equals(targetDiscord)) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().accept.linkWarningAlreadySame,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "uuid", linkedUUID,
                                "nickname", BukkitUtils.getNickname(linkedUUID, user.getEffectiveName())
                        )
                ));
            } else {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().accept.linkWarningAlreadyOthers,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "uuid", linkedUUID,
                                "nickname", BukkitUtils.getNickname(linkedUUID, user.getEffectiveName())
                        )
                ));
            }
        }

        // whitelist
        if (general().accept.addToWhitelist) {
            if (whitelistManager.addToWhitelist(nickname)) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().accept.whitelistWarningAlready,
                                "nickname", nickname
                        )
                ));
            } else {
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().accept.whitelistSuccess,
                                "nickname", nickname
                        )
                ));
            }
        }

        // guild name
        if (general().accept.changeGuildName) {
            var member = guild.getMember(user);
            if(member != null) {
                discordManager.changeName(member, nickname);
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().accept.guildNameSuccess,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "nickname", nickname
                        )
                ));
            } else {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().general.userNotOnServer,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId()
                        )
                ));
            }
        }

        // roles remove
        List<String> rolesString = new ArrayList<>();
        if (general().accept.removeRoles) {
            var member = guild.getMember(user);
            if (member != null) {
                for (String roleId : general().accept.rolesToRemove) {
                    var role = guild.getRoleById(roleId);
                    if (role == null) {
                        continue;
                    }
                    guild.removeRoleFromMember(member, role).queue();
                    rolesString.add(String.format("%s", role.getAsMention()));
                }
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().accept.guildRoleRemoveSuccess,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "roles", String.join(" ", rolesString)
                        )
                ));
            } else {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().general.userNotOnServer,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId()
                        )
                ));
            }
        }

        // roles add
        if (general().accept.addRoles) {
            var member = guild.getMember(user);
            if (member != null) {
                rolesString.clear();
                for (String roleId : general().accept.rolesToAdd) {
                    var role = guild.getRoleById(roleId);
                    if (role == null) {
                        continue;
                    }
                    guild.addRoleToMember(member, role).queue();
                    rolesString.add(String.format("%s", role.getAsMention()));
                }
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().accept.guildRoleAddSuccess,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "roles", String.join(" ", rolesString)
                        )
                ));
            } else {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().general.userNotOnServer,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId()
                        )
                ));
            }
        }

        // welcome message
        if (general().accept.sendWelcomeMessage) {
            var welcomeChannel = guild.getTextChannelById(general().accept.welcomeChannelId);
            if (welcomeChannel == null) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().general.channelNotFound,
                                "channel_id", general().accept.welcomeChannelId
                        )
                ));
            } else {
                String message = String.join("\n", lang().accept.welcomeMessage);
                var moderator = event.getMember();
                welcomeChannel.sendMessage(
                        MessageFormatter.format(
                                message,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "nickname", nickname,
                                "moderator_mention", moderator.getAsMention(),
                                "moderator_username", moderator.getUser().getAsTag(),
                                "moderator_name", moderator.getEffectiveName(),
                                "moderator_id", moderator.getId()
                        )
                ).queue();
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().accept.welcomeMessageSuccess,
                                "channel_id", general().accept.welcomeChannelId
                        )
                ));
            }
        }

        // success
        event.getHook().sendMessage(
                MessageFormatter.format(
                        lang().accept.acceptSuccess,
                        "report", String.join("\n", output)
                )
        ).queue();
    }
}
