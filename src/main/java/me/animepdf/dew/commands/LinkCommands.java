package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.User;
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
                        .addSubcommands(new SubcommandData("check", "Check if user/nickname is linked")
                                .addOptions(new OptionData(OptionType.USER, "user", "User to check", false))
                                .addOptions(new OptionData(OptionType.STRING, "nickname", "Nickname to check", false))
                        )
                        .addSubcommands(new SubcommandData("add", "Link user to nickname")
                                .addOptions(new OptionData(OptionType.USER, "user", "User to link", true))
                                .addOptions(new OptionData(OptionType.STRING, "nickname", "Nickname to link", true))
                        )
                        .addSubcommands(new SubcommandData("remove", "Link user to nickname")
                                .addOptions(new OptionData(OptionType.USER, "user", "User to link", false))
                                .addOptions(new OptionData(OptionType.STRING, "nickname", "Nickname to link", false))
                        )
                )
        );
    }

    @SlashCommand(path = "link/check", deferReply = true, deferEphemeral = true)
    public void linkCheckCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().link.enable) {
            discordManager.sendError(event,
                    lang().link.disabled
            );
            return;
        }


        User user = null;
        String nickname = null;

        // data integrity
        {
            var userRaw = event.getOption("user");
            var nicknameRaw = event.getOption("nickname");

            if (userRaw != null) {
                user = userRaw.getAsUser();
            }
            if (nicknameRaw != null && !nicknameRaw.getAsString().isEmpty()) {
                nickname = nicknameRaw.getAsString().strip();
            }

            if (nickname == null && user == null) {
                discordManager.sendError(event,
                        lang().general.noCommandArgument
                );
                return;
            }
        }

        if (user == null) {
            UUID uuid = BukkitUtils.uuidFromNickname(nickname);
            String linkedDiscord = linkManager.getLinkedDiscord(uuid);
            if (linkedDiscord != null) {
                var targetUser = discordManager.getUser(linkedDiscord);
                if (targetUser == null) {
                    discordManager.sendError(event,
                            lang().general.unknownError
                    );
                } else {
                    discordManager.sendSuccess(event,
                            MessageFormatter.format(
                                    lang().link.nicknameLinked,
                                    "discord_mention", targetUser.getAsMention(),
                                    "discord_username", targetUser.getAsTag(),
                                    "discord_name", targetUser.getEffectiveName(),
                                    "discord_id", targetUser.getId(),
                                    "nickname", nickname
                            )
                    );
                }
            } else {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.nicknameNotLinked,
                                "nickname", nickname
                        )
                );
            }
        } else if (nickname == null) {
            String discordId = user.getId();
            UUID linkedUUID = linkManager.getLinkedUUID(discordId);
            if (linkedUUID != null) {
                String linkedNickname = BukkitUtils.getNickname(linkedUUID);
                if (linkedNickname == null) {
                    discordManager.sendWarning(event,
                            MessageFormatter.format(
                                    lang().link.userLinkedNoNickname,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "uuid", linkedUUID
                            )
                    );
                } else {
                    discordManager.sendSuccess(event,
                            MessageFormatter.format(
                                    lang().link.userLinked,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "nickname", linkedNickname
                            )
                    );
                }
            } else {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.userNotLinked,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId()
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
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().link.enable) {
            discordManager.sendError(event,
                    lang().link.disabled
            );
            return;
        }

        User user = null;
        String nickname = null;

        // data integrity
        {
            var userRaw = event.getOption("user");
            var nicknameRaw = event.getOption("nickname");

            if (userRaw == null) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "link/add",
                                "arg", "user")
                );
                return;
            }
            user = userRaw.getAsUser();

            if (nicknameRaw == null || nicknameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "link/add",
                                "arg", "nickname")
                );
                return;
            }
            nickname = nicknameRaw.getAsString().strip();
        }

        // discord
        String targetDiscord = user.getId();
        UUID targetUUID = BukkitUtils.uuidFromNickname(nickname);
        String linkedDiscord = linkManager.getLinkedDiscord(targetUUID);
        UUID linkedUUID = linkManager.getLinkedUUID(targetDiscord);

        if (linkedDiscord == null && linkedUUID == null) {
            if (linkManager.linkAccount(BukkitUtils.uuidFromNickname(nickname), user.getId())) {
                discordManager.sendError(event, lang().general.unknownError);
            } else {
                discordManager.sendSuccess(event,
                        MessageFormatter.format(
                                lang().link.linkSuccess,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "uuid", targetUUID,
                                "nickname", nickname
                        )
                );
            }
        } else if (linkedUUID != null && linkedDiscord != null &&
                linkedUUID.equals(targetUUID) &&
                linkedDiscord.equals(targetDiscord)) {
            discordManager.sendWarning(event,
                    MessageFormatter.format(
                            lang().link.linkWarningAlreadySame,
                            "discord_mention", user.getAsMention(),
                            "discord_username", user.getAsTag(),
                            "discord_name", user.getEffectiveName(),
                            "discord_id", user.getId(),
                            "uuid", linkedUUID,
                            "nickname", BukkitUtils.getNickname(linkedUUID, user.getEffectiveName())
                    )
            );
        } else {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().link.linkWarningAlreadyOthers,
                            "discord_mention", user.getAsMention(),
                            "discord_username", user.getAsTag(),
                            "discord_name", user.getEffectiveName(),
                            "discord_id", user.getId(),
                            "uuid", linkedUUID,
                            "nickname", BukkitUtils.getNickname(linkedUUID, user.getEffectiveName())
                    )
            );
        }
    }

    @SlashCommand(path = "link/remove", deferReply = true, deferEphemeral = true)
    public void linkRemoveCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().link.enable) {
            discordManager.sendError(event,
                    lang().link.disabled
            );
            return;
        }

        User user = null;
        String nickname = null;

        // data integrity
        {
            var userRaw = event.getOption("user");
            var nicknameRaw = event.getOption("nickname");

            if (userRaw != null) {
                user = userRaw.getAsUser();
            }
            if (nicknameRaw != null && !nicknameRaw.getAsString().isEmpty()) {
                nickname = nicknameRaw.getAsString().strip();
            }

            if (nickname == null && user == null) {
                discordManager.sendError(event,
                        lang().general.noCommandArgument
                );
                return;
            }
        }

        // discord
        if (user == null) {
            UUID uuid = BukkitUtils.uuidFromNickname(nickname);
            String linkedDiscord = linkManager.getLinkedDiscord(uuid);
            if (linkManager.unlinkAccount(uuid)) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.unlinkNicknameNotFound,
                                "nickname", nickname
                        )
                );
            } else {
                var targetUser = discordManager.getUser(linkedDiscord);
                if (targetUser == null) {
                    discordManager.sendError(event,
                            lang().general.unknownError
                    );
                    return;
                }
                discordManager.sendSuccess(event,
                        MessageFormatter.format(
                                lang().link.unlinkNicknameSuccess,
                                "discord_mention", targetUser.getAsMention(),
                                "discord_username", targetUser.getAsTag(),
                                "discord_name", targetUser.getEffectiveName(),
                                "discord_id", targetUser.getId(),
                                "nickname", nickname
                        )
                );
            }
        } else if (nickname == null) {
            String discordId = user.getId();
            UUID linkedUUID = linkManager.getLinkedUUID(discordId);
            if (linkManager.unlinkAccount(discordId)) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().link.unlinkUserNotFound,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "nickname", nickname
                        )
                );
            } else {
                String linkedNickname = BukkitUtils.getNickname(linkedUUID);
                if (linkedNickname == null) {
                    discordManager.sendWarning(event,
                            MessageFormatter.format(
                                    lang().link.unlinkUserSuccessNoNickname,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "uuid", linkedUUID
                            )
                    );
                } else {
                    discordManager.sendSuccess(event,
                            MessageFormatter.format(
                                    lang().link.unlinkUserSuccess,
                                    "discord_mention", user.getAsMention(),
                                    "discord_username", user.getAsTag(),
                                    "discord_name", user.getEffectiveName(),
                                    "discord_id", user.getId(),
                                    "nickname", nickname
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
