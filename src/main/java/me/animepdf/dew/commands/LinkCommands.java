package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Guild;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.abstact.DEWComponent;
import me.animepdf.dew.util.BukkitUtils;
import me.animepdf.dew.util.MessageFormatter;

import java.util.Set;
import java.util.UUID;

public class LinkCommands extends DEWComponent implements SlashCommandProvider {
    public LinkCommands(DiscordEasyWhitelist plugin) {
        super(plugin);
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("link", "Remove player from the game server and discord server")
                        .addSubcommands(new SubcommandData("check", "Check if account/username is linked")
                                .addOptions(new OptionData(OptionType.USER, "member", "Member to check", false))
                                .addOptions(new OptionData(OptionType.STRING, "username", "Username to check", false))
                        )
                        .addSubcommands(new SubcommandData("add", "Link account to username")
                                .addOptions(new OptionData(OptionType.USER, "member", "Member to link", true))
                                .addOptions(new OptionData(OptionType.STRING, "username", "Username to link", true))
                        )
                        .addSubcommands(new SubcommandData("remove", "Link account to username")
                                .addOptions(new OptionData(OptionType.USER, "member", "Member to link", false))
                                .addOptions(new OptionData(OptionType.STRING, "username", "Username to link", false))
                        )
                )
        );
    }

    @SlashCommand(path = "link/check", deferReply = true, deferEphemeral = true)
    public void linkCheckCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().enableLinking) {
            discordManager.sendError(event,
                    lang().link.disabled
            );
            return;
        }


        Member member = null;
        String username = null;
        Guild guild = event.getGuild();
        if (guild == null) {
            discordManager.sendError(event,
                    lang().general.onlyOnServer
            );
            return;
        }

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw != null && memberRaw.getAsMember() != null) {
                member = memberRaw.getAsMember();
            }
            if (usernameRaw != null && !usernameRaw.getAsString().isEmpty()) {
                username = usernameRaw.getAsString().strip();
            }

            if (username == null && member == null) {
                discordManager.sendError(event,
                        lang().general.noCommandArgument
                );
                return;
            }
        }

        // discord
        if (member == null) {
            UUID uuid = BukkitUtils.uuidFromUsername(username);
            String linkedDiscord = linkManager.getLinkedDiscord(uuid);
            if (linkedDiscord != null) {
                var targetMember = guild.getMemberById(linkedDiscord);
                if (targetMember == null) {
                    var user = discordManager.getUser(linkedDiscord);
                    if (user == null) {
                        discordManager.sendError(event,
                                lang().general.unknownError
                        );
                        return;
                    }
                    discordManager.sendWarning(event,
                            MessageFormatter.format(
                                    lang().link.nicknameLinkedNotOnServer,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "username", username
                            )
                    );
                } else {
                    discordManager.sendSuccess(event,
                            MessageFormatter.format(
                                    lang().link.nicknameLinkedOnServer,
                                    "discord_mention", targetMember.getAsMention(),
                                    "discord_username", targetMember.getUser().getAsTag(),
                                    "discord_name", targetMember.getEffectiveName(),
                                    "discord_id", targetMember.getId(),
                                    "username", username
                            )
                    );
                }
            } else {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.nicknameNotLinked,
                                "username", username
                        )
                );
            }
        } else if (username == null) {
            String discordId = member.getId();
            UUID linkedUUID = linkManager.getLinkedUUID(discordId);
            if (linkedUUID != null) {
                String linkedUsername = BukkitUtils.getNickname(linkedUUID);
                if (linkedUsername == null) {
                    discordManager.sendWarning(event,
                            MessageFormatter.format(
                                    lang().link.userLinkedNoNickname,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "uuid", linkedUUID
                            )
                    );
                } else {
                    discordManager.sendSuccess(event,
                            MessageFormatter.format(
                                    lang().link.userLinked,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "username", linkedUsername
                            )
                    );
                }
            } else {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.userNotLinked,
                                "discord_mention", member.getAsMention(),
                                "discord_username", member.getUser().getAsTag(),
                                "discord_name", member.getEffectiveName(),
                                "discord_id", member.getId()
                        )
                );
            }
        } else {
            discordManager.sendError(event,
                    lang().general.tooManyCommandArguments
            );
        }
    }

    @SlashCommand(path = "link/add", deferReply = true, deferEphemeral = true)
    public void linkAddCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().enableLinking) {
            discordManager.sendError(event,
                    lang().link.disabled
            );
            return;
        }

        Member member = null;
        String username = null;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw == null || memberRaw.getAsMember() == null) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "link/add",
                                "arg", "member")
                );
                return;
            }
            member = memberRaw.getAsMember();

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "link/add",
                                "arg", "username")
                );
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // discord
        String targetDiscord = member.getId();
        UUID targetUUID = BukkitUtils.uuidFromUsername(username);
        String linkedDiscord = linkManager.getLinkedDiscord(targetUUID);
        UUID linkedUUID = linkManager.getLinkedUUID(targetDiscord);

        if (linkedDiscord == null && linkedUUID == null) {
            if (linkManager.linkAccount(BukkitUtils.uuidFromUsername(username), member.getId())) {
                discordManager.sendError(event, lang().general.unknownError);
            } else {
                discordManager.sendSuccess(event,
                        MessageFormatter.format(
                                lang().link.linkSuccess,
                                "discord_mention", member.getAsMention(),
                                "discord_username", member.getUser().getAsTag(),
                                "discord_name", member.getEffectiveName(),
                                "discord_id", member.getId(),
                                "uuid", targetUUID,
                                "username", username
                        )
                );
            }
        } else if (linkedUUID != null && linkedDiscord != null &&
                linkedUUID.equals(targetUUID) &&
                linkedDiscord.equals(targetDiscord)) {
            discordManager.sendWarning(event,
                    MessageFormatter.format(
                            lang().link.linkWarningAlreadySame,
                            "discord_mention", member.getAsMention(),
                            "discord_username", member.getUser().getAsTag(),
                            "discord_name", member.getEffectiveName(),
                            "discord_id", member.getId(),
                            "uuid", linkedUUID,
                            "username", BukkitUtils.getNickname(linkedUUID, member.getEffectiveName())
                    )
            );
        } else {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().link.linkWarningAlreadyOthers,
                            "discord_mention", member.getAsMention(),
                            "discord_username", member.getUser().getAsTag(),
                            "discord_name", member.getEffectiveName(),
                            "discord_id", member.getId(),
                            "uuid", linkedUUID,
                            "username", BukkitUtils.getNickname(linkedUUID, member.getEffectiveName())
                    )
            );
        }
    }

    @SlashCommand(path = "link/remove", deferReply = true, deferEphemeral = true)
    public void linkRemoveCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().enableLinking) {
            discordManager.sendError(event,
                    lang().link.disabled
            );
            return;
        }

        Member member = null;
        String username = null;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw != null && memberRaw.getAsMember() != null) {
                member = memberRaw.getAsMember();
            }
            if (usernameRaw != null && !usernameRaw.getAsString().isEmpty()) {
                username = usernameRaw.getAsString().strip();
            }

            if (username == null && member == null) {
                discordManager.sendError(event,
                        lang().general.noCommandArgument
                );
                return;
            }
        }

        // discord
        if (member == null) {
            UUID uuid = BukkitUtils.uuidFromUsername(username);
            String linkedDiscord = linkManager.getLinkedDiscord(uuid);
            if (linkManager.unlinkAccount(uuid)) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.unlinkNicknameNotFound,
                                "username", username
                        )
                );
            } else {
                var user = discordManager.getUser(linkedDiscord);
                if (user == null) {
                    discordManager.sendError(event,
                            lang().general.unknownError
                    );
                    return;
                }
                discordManager.sendSuccess(event,
                        MessageFormatter.format(
                                lang().link.unlinkNicknameSuccess,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "username", username
                        )
                );
            }
        } else if (username == null) {
            String discordId = member.getId();
            UUID linkedUUID = linkManager.getLinkedUUID(discordId);
            if (linkManager.unlinkAccount(discordId)) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.unlinkUserNotFound,
                                "discord_mention", member.getAsMention(),
                                "discord_username", member.getUser().getAsTag(),
                                "discord_name", member.getEffectiveName(),
                                "discord_id", member.getId(),
                                "username", username
                        )
                );
            } else {
                String linkedUsername = BukkitUtils.getNickname(linkedUUID);
                if (linkedUsername == null) {
                    discordManager.sendWarning(event,
                            MessageFormatter.format(
                                    lang().link.unlinkUserSuccessNoNickname,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "uuid", linkedUUID
                            )
                    );
                } else {
                    discordManager.sendSuccess(event,
                            MessageFormatter.format(
                                    lang().link.unlinkUserSuccess,
                                    "discord_mention", member.getAsMention(),
                                    "discord_username", member.getUser().getAsTag(),
                                    "discord_name", member.getEffectiveName(),
                                    "discord_id", member.getId(),
                                    "username", username
                            )
                    );
                }
            }
        } else {
            discordManager.sendError(event,
                    lang().general.tooManyCommandArguments
            );
        }
    }
}
