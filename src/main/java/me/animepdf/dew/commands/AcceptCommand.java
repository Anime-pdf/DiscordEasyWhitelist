package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
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
                new PluginSlashCommand(this.plugin, new CommandData("accept", "Accept a mimocrocodile to the server")
                        .addOption(OptionType.USER, "member", "Member to add", true)
                        .addOption(OptionType.STRING, "username", "Minecraft username to add to whitelist", true)
                )
        );
    }

    @SlashCommand(path = "accept", deferReply = true, deferEphemeral = true)
    public void acceptCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        Member member;
        String username;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw == null || memberRaw.getAsMember() == null) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "accept",
                                "arg", "member")
                );
                return;
            }
            member = memberRaw.getAsMember();

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "accept",
                                "arg", "username")
                );
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        var guild = member.getGuild();
        List<String> output = new ArrayList<>();

        // discord
        if (general().enableLinking) {
            String targetDiscord = member.getId();
            UUID targetUUID = BukkitUtils.uuidFromUsername(username);
            String linkedDiscord = linkManager.getLinkedDiscord(targetUUID);
            UUID linkedUUID = linkManager.getLinkedUUID(targetDiscord);

            if (linkedDiscord == null && linkedUUID == null) {
                if (linkManager.linkAccount(BukkitUtils.uuidFromUsername(username), member.getId())) {
                    discordManager.sendError(event, lang().general.unknownError);
                    return;
                } else {
                    output.add(discordManager.appendSuccess(
                            MessageFormatter.format(
                                    lang().accept.linkSuccess,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "uuid", targetUUID,
                                    "username", username
                            )
                    ));
                }
            } else if (linkedUUID != null && linkedDiscord != null &&
                    linkedUUID.equals(targetUUID) &&
                    linkedDiscord.equals(targetDiscord)) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().accept.linkWarningAlreadySame,
                                "discord_mention", member.getAsMention(),
                                "discord_username", member.getUser().getAsTag(),
                                "discord_name", member.getEffectiveName(),
                                "discord_id", member.getId(),
                                "uuid", linkedUUID,
                                "username", BukkitUtils.getNickname(linkedUUID, member.getEffectiveName())
                        )
                ));
            } else {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().accept.linkWarningAlreadyOthers,
                                "discord_mention", member.getAsMention(),
                                "discord_username", member.getUser().getAsTag(),
                                "discord_name", member.getEffectiveName(),
                                "discord_id", member.getId(),
                                "uuid", linkedUUID,
                                "username", BukkitUtils.getNickname(linkedUUID, member.getEffectiveName())
                        )
                ));
            }
        }

        // whitelist
        if (general().accept.addToWhitelist) {
            if (whitelistManager.addToWhitelist(username)) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().accept.whitelistWarningAlready,
                                "username", username
                        )
                ));
            } else {
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().accept.whitelistSuccess,
                                "username", username
                        )
                ));
            }
        }

        // guild name
        if (general().accept.changeGuildName) {
            discordManager.changeName(member, username);
            output.add(discordManager.appendSuccess(
                    MessageFormatter.format(
                            lang().accept.guildNameSuccess,
                            "discord_mention", member.getAsMention(),
                            "discord_username", member.getUser().getAsTag(),
                            "discord_name", member.getEffectiveName(),
                            "discord_id", member.getId(),
                            "username", username
                    )
            ));
        }

        // roles remove
        List<String> rolesString = new ArrayList<>();
        if (general().accept.removeRoles) {
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
                            "discord_mention", member.getAsMention(),
                            "discord_username", member.getUser().getAsTag(),
                            "discord_name", member.getEffectiveName(),
                            "discord_id", member.getId(),
                            "roles", String.join(" ", rolesString)
                    )
            ));
        }

        // roles add
        if (general().accept.addRoles) {
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
                            "discord_mention", member.getAsMention(),
                            "discord_username", member.getUser().getAsTag(),
                            "discord_name", member.getEffectiveName(),
                            "discord_id", member.getId(),
                            "roles", String.join(" ", rolesString)
                    )
            ));
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
                                "discord_mention", member.getAsMention(),
                                "discord_username", member.getUser().getAsTag(),
                                "discord_name", member.getEffectiveName(),
                                "discord_id", member.getId(),
                                "username", username,
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
