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
                        .addOption(OptionType.USER, "user", "User to remove", true)
                        .addOption(OptionType.STRING, "nickname", "Minecraft nickname", false)
                        .addOption(OptionType.STRING, "reason", "Reason", false)
                )
        );
    }

    @SlashCommand(path = "remove", deferReply = true, deferEphemeral = true)
    public void removeCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().remove.enable) {
            discordManager.sendError(event,
                    lang().remove.disabled
            );
            return;
        }

        boolean explicitNickname = false;
        User user;
        String nickname;
        @Nullable String reason;

        // data integrity
        {
            var userRaw = event.getOption("user");
            var nicknameRaw = event.getOption("nickname");
            var reasonRaw = event.getOption("reason");

            if (userRaw == null) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "remove",
                                "arg", "user")
                );
                return;
            }
            user = userRaw.getAsUser();

            if (nicknameRaw == null || nicknameRaw.getAsString().isEmpty()) {
                var member = event.getGuild().getMember(user);
                if (member != null) {
                    nickname = member.getEffectiveName();
                } else {
                    nickname = user.getEffectiveName();
                }
            } else {
                explicitNickname = true;
                nickname = nicknameRaw.getAsString().strip();
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
                    - Linked to this user -> unlink, proceed
                    - Nickname or discord linked to someone else -> error
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
        if (general().remove.unlinkFromNickname) {
            String targetDiscord = user.getId();
            UUID targetUUID = BukkitUtils.uuidFromNickname(nickname);
            UUID linkedUUID = linkManager.getLinkedUUID(user.getId());
            String linkedDiscord = linkManager.getLinkedDiscord(targetUUID);

            if (explicitNickname) {
                if (linkedDiscord == null && linkedUUID == null) {
                    output.add(discordManager.appendWarning(
                            MessageFormatter.format(
                                    lang().remove.linkWarningNotLinked,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "nickname", nickname
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
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "nickname", nickname
                            )
                    ));
                } else {
                    discordManager.sendError(event,
                            MessageFormatter.format(
                                    lang().remove.linkErrorOther,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "nickname", nickname
                            )
                    );
                    return;
                }
            } else {
                if (linkedUUID != null) {
                    var nicknameTemp = BukkitUtils.getNickname(linkedUUID);
                    if (nicknameTemp != null) {
                        if (linkManager.unlinkAccount(targetDiscord)) {
                            discordManager.sendError(event, lang().general.unknownError);
                            return;
                        }
                        output.add(discordManager.appendSuccess(
                                MessageFormatter.format(
                                        lang().remove.linkSuccess,
                                        "discord_mention", user.getAsMention(),
                                        "discord_username", user.getAsTag(),
                                        "discord_name", user.getEffectiveName(),
                                        "discord_id", user.getId(),
                                        "nickname", nicknameTemp
                                )
                        ));
                        nickname = nicknameTemp;
                    } else {
                        discordManager.sendError(event,
                                MessageFormatter.format(
                                        lang().remove.linkErrorNeverPlayed,
                                        "discord_mention", user.getAsMention(),
                                        "discord_username", user.getAsTag(),
                                        "discord_name", user.getEffectiveName(),
                                        "discord_id", user.getId()
                                ));
                        return;
                    }
                } else {
                    if (general().remove.fallbackToGuildUsername) {
                        output.add(discordManager.appendWarning(
                                MessageFormatter.format(
                                        lang().remove.linkWarningNotLinkedFallback,
                                        "discord_mention", user.getAsMention(),
                                        "discord_username", user.getAsTag(),
                                        "discord_name", user.getEffectiveName(),
                                        "discord_id", user.getId(),
                                        "fallback", nickname
                                )
                        ));
                    } else {
                        discordManager.sendError(event,
                                MessageFormatter.format(
                                        lang().remove.linkErrorNotLinked,
                                        "discord_mention", user.getAsMention(),
                                        "discord_username", user.getAsTag(),
                                        "discord_name", user.getEffectiveName(),
                                        "discord_id", user.getId()
                                )
                        );
                    }
                }
            }
        }

        // whitelist
        if (general().remove.removeFromWhitelist) {
            if (whitelistManager.removeFromWhitelist(nickname)) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().remove.whitelistWarningAlready,
                                "nickname", nickname
                        )
                ));
            } else {
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().remove.whitelistSuccess,
                                "nickname", nickname
                        )
                ));
            }
        }

        // server kick
        if (general().remove.kickFromGameIfOnline) {
            final String tempNickname = nickname;
            Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
                var player = Bukkit.getPlayer(tempNickname);
                if (player != null) {
                    player.kick(lang().remove.kickMessage);
                }
            });
        }

        // ban message
        var guild = event.getGuild();
        if (general().remove.sendRemoveMessage) {
            var banChannel = guild.getTextChannelById(general().remove.removeChannelId);
            if (banChannel == null) {
                output.add(discordManager.appendWarning(
                        MessageFormatter.format(
                                lang().general.channelNotFound,
                                "channel_id", general().remove.removeChannelId
                        )
                ));
            } else {
                var moderator = event.getMember();
                if (reason != null) {
                    banChannel.sendMessage(
                            MessageFormatter.format(
                                    String.join("\n", lang().remove.removeMessageReason),
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "nickname", nickname,
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
                                    String.join("\n", lang().remove.removeMessage),
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
                }
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().remove.removeMessageSuccess,
                                "channel_id", general().remove.removeChannelId
                        )
                ));
            }
        }

        // direct message
        CompletableFuture<Void> dmFuture = null;
        if (general().remove.sendDirectMessage) {
            dmFuture = user.openPrivateChannel().submit()
                    .thenCompose(privateChannel -> {
                        if (reason != null) {
                            return privateChannel.sendMessage(
                                    MessageFormatter.format(
                                            String.join("\n", lang().remove.removeDirectMessageReason),
                                            "reason", reason
                                    )
                            ).submit();
                        } else {
                            return privateChannel.sendMessage(String.join("\n", lang().remove.removeDirectMessage)).submit();
                        }
                    })
                    .thenAccept(message -> {
                        output.add(discordManager.appendSuccess(
                                MessageFormatter.format(
                                        lang().remove.removeDirectMessageSuccess,
                                        "discord_mention", user.getAsMention(),
                                        "discord_username", user.getAsTag(),
                                        "discord_name", user.getEffectiveName(),
                                        "discord_id", user.getId()
                                )
                        ));
                    })
                    .exceptionally(throwable -> {
                        output.add(discordManager.appendWarning(
                                MessageFormatter.format(
                                        lang().remove.removeDirectMessageFailure,
                                        "discord_mention", user.getAsMention(),
                                        "discord_username", user.getAsTag(),
                                        "discord_name", user.getEffectiveName(),
                                        "discord_id", user.getId()
                                )
                        ));
                        return null;
                    });
        }

        // ban
        if (general().remove.banFromGuild) {
            var member = event.getGuild().getMember(user);
            if (member != null) {
                discordManager.banMember(member, lang().remove.guildBanReason);
                output.add(discordManager.appendSuccess(
                        MessageFormatter.format(
                                lang().remove.guildBanSuccess,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId()
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
