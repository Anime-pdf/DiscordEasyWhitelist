package me.animepdf.dew.listeners;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.dependencies.jda.api.audit.ActionType;
import github.scarsz.discordsrv.dependencies.jda.api.audit.AuditLogEntry;
import github.scarsz.discordsrv.dependencies.jda.api.events.guild.member.GuildMemberRemoveEvent;
import github.scarsz.discordsrv.dependencies.jda.api.hooks.ListenerAdapter;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.config.ConfigContainer;
import me.animepdf.dew.config.GeneralConfig;
import me.animepdf.dew.config.LanguageConfig;
import me.animepdf.dew.managers.DiscordManager;
import me.animepdf.dew.managers.LinkManager;
import me.animepdf.dew.managers.WhitelistManager;
import me.animepdf.dew.util.BukkitUtils;
import me.animepdf.dew.util.MessageFormatter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class DiscordListener extends ListenerAdapter {
    private final DiscordEasyWhitelist plugin;
    protected final ConfigContainer configContainer;
    protected final WhitelistManager whitelistManager;
    protected final LinkManager linkManager;
    protected final DiscordManager discordManager;

    public DiscordListener(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
        this.whitelistManager = plugin.getWhitelistManager();
        this.linkManager = plugin.getLinkManager();
        this.discordManager = plugin.getDiscordManager();
    }

    protected LanguageConfig lang() {
        return this.configContainer.getLanguageConfig();
    }

    protected GeneralConfig general() {
        return this.configContainer.getGeneralConfig();
    }

    @Override
    public void onGuildMemberRemove(@NotNull GuildMemberRemoveEvent event) {
        if (!general().leave.enable)
            return;

        var guild = event.getGuild();
        var user = event.getUser();

        AtomicBoolean isBan = new AtomicBoolean(false);
        AtomicBoolean isDEW = new AtomicBoolean(false);

        guild.retrieveAuditLogs()
                .queueAfter(2, TimeUnit.SECONDS, (logs) -> {
                    for (AuditLogEntry log : logs) {
                        if (log.getTargetIdLong() == event.getUser().getIdLong()) {
                            isBan.set(log.getType() == ActionType.BAN);
                            if (isBan.get()) {
                                if (log.getUser() != null) {
                                    isDEW.set(log.getUser().getId().equals(DiscordSRV.getPlugin().getJda().getSelfUser().getId()));
                                }
                            }
                            break;
                        }
                    }

                    if (isDEW.get())
                        return;

                    List<String> output = Collections.synchronizedList(new ArrayList<>());
                    @Nullable String username = null;

                    if (general().enableLinking) {
                        String targetDiscord = user.getId();
                        UUID linkedUUID = linkManager.getLinkedUUID(user.getId());
                        if (linkedUUID != null) {
                            var usernameTemp = BukkitUtils.getNickname(linkedUUID);
                            if (general().leave.unlinkIfAvailable) {
                                if (usernameTemp != null) {
                                    username = usernameTemp;
                                    if (linkManager.unlinkAccount(targetDiscord)) {
                                        output.add(discordManager.appendError(lang().general.unknownError));
                                    } else {
                                        output.add(discordManager.appendSuccess(
                                                MessageFormatter.format(
                                                        lang().leave.reportUnlinked,
                                                        "discord_mention", user.getAsMention(),
                                                        "discord_username", user.getAsTag(),
                                                        "discord_name", user.getEffectiveName(),
                                                        "discord_id", user.getId(),
                                                        "username", username
                                                )
                                        ));
                                    }
                                } else {
                                    output.add(discordManager.appendWarning(
                                            MessageFormatter.format(
                                                    lang().leave.reportWarningNameNotResolved,
                                                    "discord_mention", user.getAsMention(),
                                                    "discord_username", user.getAsTag(),
                                                    "discord_name", user.getEffectiveName(),
                                                    "discord_id", user.getId()
                                            )
                                    ));
                                    if (linkManager.unlinkAccount(targetDiscord)) {
                                        output.add(discordManager.appendError(lang().general.unknownError));
                                    } else {
                                        output.add(discordManager.appendWarning(
                                                MessageFormatter.format(
                                                        lang().leave.reportUnlinkedNameNotResolved,
                                                        "discord_mention", user.getAsMention(),
                                                        "discord_username", user.getAsTag(),
                                                        "discord_name", user.getEffectiveName(),
                                                        "discord_id", user.getId(),
                                                        "uuid", linkedUUID
                                                )
                                        ));
                                    }
                                }
                            }
                        } else {
                            output.add(discordManager.appendWarning(
                                    MessageFormatter.format(
                                            lang().leave.reportWarningNotLinked,
                                            "discord_mention", user.getAsMention(),
                                            "discord_username", user.getAsTag(),
                                            "discord_name", user.getEffectiveName(),
                                            "discord_id", user.getId()
                                    )
                            ));
                        }
                    }

                    if (general().leave.removeFromWhitelist) {
                        if (username != null) {
                            if (whitelistManager.removeFromWhitelist(username)) {
                                output.add(discordManager.appendWarning(
                                        MessageFormatter.format(
                                                lang().leave.reportWarningNotInWhitelist,
                                                "username", username
                                        )
                                ));
                            } else {
                                output.add(discordManager.appendSuccess(
                                        MessageFormatter.format(
                                                lang().leave.reportRemovedFromWhitelist,
                                                "username", username
                                        )
                                ));
                            }
                        }
                    }

                    if (general().leave.kickFromGameIfOnline && username != null) {
                        final String tempUser = username;
                        Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
                            var player = Bukkit.getPlayer(tempUser);
                            if (player != null) {
                                player.kick(lang().leave.kickMessage);
                            }
                        });
                    }

                    if (general().leave.banFromGuild) {
                        if (isBan.get()) {
                            output.add(discordManager.appendWarning(
                                    MessageFormatter.format(
                                            lang().leave.reportWarningAlreadyBanned,
                                            "discord_mention", user.getAsMention(),
                                            "discord_username", user.getAsTag(),
                                            "discord_name", user.getEffectiveName(),
                                            "discord_id", user.getId()
                                    )
                            ));
                        } else {
                            discordManager.banUser(guild, user, lang().leave.guildBanReason);
                            output.add(discordManager.appendSuccess(
                                    MessageFormatter.format(
                                            lang().leave.reportBannedFromGuild,
                                            "discord_mention", user.getAsMention(),
                                            "discord_username", user.getAsTag(),
                                            "discord_name", user.getEffectiveName(),
                                            "discord_id", user.getId()
                                    )
                            ));
                        }
                    }

                    if (general().leave.sendLeaveMessage) {
                        var leaveChannel = guild.getTextChannelById(general().leave.leaveChannelId);
                        if (leaveChannel == null) {
                            output.add(discordManager.appendWarning(
                                    MessageFormatter.format(
                                            lang().general.channelNotFound,
                                            "channel_id", general().leave.leaveChannelId
                                    )
                            ));
                        } else {
                            if (username == null) {
                                leaveChannel.sendMessage(
                                        MessageFormatter.format(
                                                String.join("\n", lang().leave.leaveMessageNameNotResolved),
                                                "discord_mention", user.getAsMention(),
                                                "discord_username", user.getAsTag(),
                                                "discord_name", user.getEffectiveName(),
                                                "discord_id", user.getId()
                                        )
                                ).queue();
                            } else {
                                leaveChannel.sendMessage(
                                        MessageFormatter.format(
                                                String.join("\n", lang().leave.leaveMessage),
                                                "discord_mention", user.getAsMention(),
                                                "discord_username", user.getAsTag(),
                                                "discord_name", user.getEffectiveName(),
                                                "discord_id", user.getId(),
                                                "username", username
                                        )
                                ).queue();
                            }
                            output.add(discordManager.appendSuccess(
                                    MessageFormatter.format(
                                            lang().leave.reportMessageSuccess,
                                            "channel_id", general().leave.leaveChannelId
                                    )
                            ));
                        }
                    }

                    CompletableFuture<Void> dmFuture = null;
                    if (general().leave.sendDirectMessage) {
                        dmFuture = user.openPrivateChannel().submit()
                                .thenCompose(privateChannel -> {
                                    return privateChannel.sendMessage(String.join("\n", lang().leave.leaveDirectMessage)).submit();
                                })
                                .thenAccept(message -> {
                                    output.add(discordManager.appendSuccess(
                                            MessageFormatter.format(
                                                    lang().leave.reportDirectMessageSuccess,
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
                                                    lang().leave.reportWarningDirectMessagesFailure,
                                                    "discord_mention", user.getAsMention(),
                                                    "discord_username", user.getAsTag(),
                                                    "discord_name", user.getEffectiveName(),
                                                    "discord_id", user.getId()
                                            )
                                    ));
                                    return null;
                                });
                    }

                    if (!general().leave.sendReportMessage)
                        return;

                    var reportChannel = guild.getTextChannelById(general().leave.reportChannelId);
                    if (reportChannel == null) {
                        this.plugin.getLogger().severe("Report channel can't be found, check your general.yml");
                        return;
                    }

                    if (dmFuture != null) {
                        dmFuture.whenComplete((unused, throwable) -> {
                            reportChannel.sendMessage(
                                    MessageFormatter.format(
                                            lang().leave.reportMessage,
                                            "discord_mention", user.getAsMention(),
                                            "discord_username", user.getAsTag(),
                                            "discord_name", user.getEffectiveName(),
                                            "discord_id", user.getId(),
                                            "report", String.join("\n", output)
                                    )
                            ).queue();
                        });
                    } else {
                        reportChannel.sendMessage(
                                MessageFormatter.format(
                                        lang().leave.reportMessage,
                                        "discord_mention", user.getAsMention(),
                                        "discord_username", user.getAsTag(),
                                        "discord_name", user.getEffectiveName(),
                                        "discord_id", user.getId(),
                                        "report", String.join("\n", output)
                                )
                        ).queue();
                    }
                });

    }
}
