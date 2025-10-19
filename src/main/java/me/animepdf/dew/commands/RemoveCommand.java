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
import me.animepdf.dew.util.BukkitUtils;
import me.animepdf.dew.util.MessageFormatter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class RemoveCommand extends DEWComponent implements SlashCommandProvider {
    public RemoveCommand(DiscordEasyWhitelist plugin) {
        super(plugin);
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("remove", "Remove player from the game server and discord server")
                        .addOption(OptionType.USER, "member", "Member to remove", true)
                        .addOption(OptionType.STRING, "username", "Minecraft username, will be taken from guild nickname if empty", false)
                        .addOption(OptionType.STRING, "reason", "Reason", false)
                )
        );
    }

    @SlashCommand(path = "remove", deferReply = true, deferEphemeral = true)
    public void removeCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        boolean explicitUsername = false;
        Member member;
        String username;
        @Nullable String reason;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");
            var reasonRaw = event.getOption("reason");

            if (memberRaw == null || memberRaw.getAsMember() == null) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "remove",
                                "arg", "member")
                );
                return;
            }
            member = memberRaw.getAsMember();

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                username = member.getEffectiveName();
            } else {
                explicitUsername = true;
                username = usernameRaw.getAsString().strip();
            }

            if (reasonRaw == null || reasonRaw.getAsString().isEmpty()) {
                reason = null;
            } else {
                reason = reasonRaw.getAsString();
            }
        }

        List<String> output = Collections.synchronizedList(new ArrayList<>());

        // discord
        /*
            If moderator entered a nickname
                Check if nickname linked to discord
                    - Linked to this member -> unlink, proceed
                    - Username or discord linked to someone else -> error
                    - Not linked -> warning, proceed
            If moderator didn't enter a nickname
                Check if discord linked to nickname
                    If linked, try to resolve nickname
                        - Nickname can be resolved -> unlink, proceed
                        - Nickname can't be resolved -> warning, proceed
                    If not linked, check if we can fallback
                        - Fallback isn't turned on -> error
                        - Fallback turned on -> warning, fallback to discord name, proceed
         */
        if (general().enableLinking) {
            String targetDiscord = member.getId();
            UUID targetUUID = BukkitUtils.uuidFromUsername(username);
            UUID linkedUUID = linkManager.getLinkedUUID(member.getId());
            String linkedDiscord = linkManager.getLinkedDiscord(targetUUID);

            if (explicitUsername) {
                if (linkedDiscord == null && linkedUUID == null) {
                    output.add(discordManager.appendWarning(
                            MessageFormatter.format(
                                    lang().remove.linkWarningNotLinked,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "username", username
                            )
                    ));
                } else if (linkedUUID != null && linkedDiscord != null &&
                        linkedUUID.equals(targetUUID) &&
                        linkedDiscord.equals(targetDiscord)) {
                    if (linkManager.unlinkAccount(targetDiscord)) {
                        discordManager.sendError(event, lang().general.unknownError);
                        return;
                    }
                    output.add(discordManager.appendSuccess(
                            MessageFormatter.format(
                                    lang().remove.linkSuccess,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "username", username
                            )
                    ));
                } else {
                    discordManager.sendError(event,
                            MessageFormatter.format(
                                    lang().remove.linkErrorOther,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "username", username
                            )
                    );
                    return;
                }
            } else {
                if (linkedUUID != null) {
                    var usernameTemp = BukkitUtils.getNickname(linkedUUID);
                    if (usernameTemp != null) {
                        if (linkManager.unlinkAccount(targetDiscord)) {
                            discordManager.sendError(event, lang().general.unknownError);
                            return;
                        }
                        output.add(discordManager.appendSuccess(
                                MessageFormatter.format(
                                        lang().remove.linkSuccess,
                                        "discord_mention", member.getAsMention(),
                                        "discord_username", member.getUser().getAsTag(),
                                        "discord_name", member.getEffectiveName(),
                                        "discord_id", member.getId(),
                                        "username", usernameTemp
                                )
                        ));
                        username = usernameTemp;
                    } else {
                        discordManager.sendError(event,
                                MessageFormatter.format(
                                        lang().remove.linkErrorNeverPlayed,
                                        "discord_mention", member.getAsMention(),
                                        "discord_username", member.getUser().getAsTag(),
                                        "discord_name", member.getEffectiveName(),
                                        "discord_id", member.getId()
                                ));
                        return;
                    }
                } else {
                    if (general().remove.fallbackToUsernameOnRemove) {
                        output.add(discordManager.appendWarning(
                                MessageFormatter.format(
                                        lang().remove.linkWarningNotLinkedFallback,
                                        "discord_mention", member.getAsMention(),
                                        "discord_username", member.getUser().getAsTag(),
                                        "discord_name", member.getEffectiveName(),
                                        "discord_id", member.getId(),
                                        "fallback", username
                                )
                        ));
                    } else {
                        discordManager.sendError(event,
                                MessageFormatter.format(
                                        lang().remove.linkErrorNotLinked,
                                        "discord_mention", member.getAsMention(),
                                        "discord_username", member.getUser().getAsTag(),
                                        "discord_name", member.getEffectiveName(),
                                        "discord_id", member.getId()
                                )
                        );
                    }
                }
            }
        }

        // whitelist
        if (general().remove.removeFromWhitelist) {
            if (whitelistManager.removeFromWhitelist(username)) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().remove.whitelistWarningAlready,
                                "username", username
                        )
                ));
            } else {
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().remove.whitelistSuccess,
                                "username", username
                        )
                ));
            }
        }

        // server kick
        if (general().remove.kickFromServerIfOnline) {
            final String tempUser = username;
            Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
                var player = Bukkit.getPlayer(tempUser);
                if (player != null) {
                    player.kick(lang().remove.kickMessage);
                }
            });
        }

        // ban message
        var guild = member.getGuild();
        if (general().remove.sendBanMessage) {
            var banChannel = guild.getTextChannelById(general().remove.bansChannelId);
            if (banChannel == null) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().general.channelNotFound,
                                "channel_id", general().remove.bansChannelId
                        )
                ));
            } else {
                var moderator = event.getMember();
                if (reason != null) {
                    banChannel.sendMessage(
                            MessageFormatter.format(
                                    String.join("\n", lang().remove.banMessageReason),
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "username", username,
                                    "moderator_mention", moderator.getAsMention(),
                                    "moderator_username", moderator.getUser().getAsTag(),
                                    "moderator_name", moderator.getEffectiveName(),
                                    "moderator_id", moderator.getId(),
                                    "reason", reason
                            )
                    ).queue();
                } else {
                    banChannel.sendMessage(
                            MessageFormatter.format(
                                    String.join("\n", lang().remove.banMessage),
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
                }
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().remove.banMessageSuccess,
                                "channel_id", general().remove.bansChannelId
                        )
                ));
            }
        }

        // direct message
        CompletableFuture<Void> dmFuture = null;
        if (general().remove.sendDirectBanMessage) {
            dmFuture = member.getUser().openPrivateChannel().submit()
                    .thenCompose(privateChannel -> {
                        if (reason != null) {
                            return privateChannel.sendMessage(
                                    MessageFormatter.format(
                                            String.join("\n", lang().remove.banDirectMessageReason),
                                            "reason", reason
                                    )
                            ).submit();
                        } else {
                            return privateChannel.sendMessage(String.join("\n", lang().remove.banDirectMessage)).submit();
                        }
                    })
                    .thenAccept(message -> {
                        output.add(discordManager.appendSuccess(
                                MessageFormatter.format(
                                        lang().remove.banDirectMessageSuccess,
                                        "discord_mention", member.getAsMention(),
                                        "discord_username", member.getUser().getAsTag(),
                                        "discord_name", member.getEffectiveName(),
                                        "discord_id", member.getId()
                                )
                        ));
                    })
                    .exceptionally(throwable -> {
                        output.add(discordManager.appendWarning(
                                MessageFormatter.format(
                                        lang().remove.banDirectMessageFailure,
                                        "discord_mention", member.getAsMention(),
                                        "discord_username", member.getUser().getAsTag(),
                                        "discord_name", member.getEffectiveName(),
                                        "discord_id", member.getId()
                                )
                        ));
                        return null;
                    });
        }

        // ban
        if (general().remove.banOnDiscordServer) {
            discordManager.banMember(member, lang().remove.guildBanReason);
            output.add(discordManager.appendSuccess(
                    MessageFormatter.format(
                            lang().remove.guildBanSuccess,
                            "discord_mention", member.getAsMention(),
                            "discord_username", member.getUser().getAsTag(),
                            "discord_name", member.getEffectiveName(),
                            "discord_id", member.getId()
                    )
            ));
        }

        // success
        if (dmFuture != null) {
            dmFuture.whenComplete((unused, throwable) -> {
                event.getHook().sendMessage(
                        MessageFormatter.format(
                                lang().remove.removeSuccess,
                                "report", String.join("\n", output)
                        )
                ).queue();
            });
        } else {
            event.getHook().sendMessage(
                    MessageFormatter.format(
                            lang().remove.removeSuccess,
                            "report", String.join("\n", output)
                    )
            ).queue();
        }
    }
}
