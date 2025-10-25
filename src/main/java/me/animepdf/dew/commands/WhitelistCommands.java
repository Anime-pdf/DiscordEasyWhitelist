package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.abstact.DEWComponent;
import me.animepdf.dew.util.MessageFormatter;
import org.bukkit.Bukkit;

import java.util.Set;

public class WhitelistCommands extends DEWComponent implements SlashCommandProvider {
    public WhitelistCommands(DiscordEasyWhitelist plugin) {
        super(plugin);
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("whitelist", "Manipulate whitelist")
                        .addSubcommands(new SubcommandData("check", "Check if nickname is in whitelist")
                                .addOption(OptionType.STRING, "nickname", "Nickname to check", true)
                        )
                        .addSubcommands(new SubcommandData("add", "Add nickname to the whitelist")
                                .addOption(OptionType.STRING, "nickname", "Minecraft nickname to add to whitelist", true)
                        )
                        .addSubcommands(new SubcommandData("remove", "Remove nickname from the whitelist")
                                .addOption(OptionType.STRING, "nickname", "Minecraft nickname to remove from whitelist", true)
                        )
                )
        );
    }

    @SlashCommand(path = "whitelist/check", deferReply = true, deferEphemeral = true)
    public void whitelistCheckCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().whitelist.enable) {
            discordManager.sendError(event,
                    lang().whitelist.disabled
            );
            return;
        }

        String nickname;

        // data integrity
        {
            var nicknameRaw = event.getOption("nickname");

            if (nicknameRaw == null || nicknameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "whitelist/check",
                                "arg", "nickname")
                );
                return;
            }
            nickname = nicknameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelistManager.isInWhitelist(nickname)) {
            discordManager.sendSuccess(event,
                    MessageFormatter.format(
                            lang().whitelist.containsNickname,
                            "nickname", nickname
                    )
            );
        } else {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().whitelist.notContainsNickname,
                            "nickname", nickname
                    )
            );
        }
    }

    @SlashCommand(path = "whitelist/add", deferReply = true, deferEphemeral = true)
    public void whitelistAddCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().whitelist.enable) {
            discordManager.sendError(event,
                    lang().whitelist.disabled
            );
            return;
        }

        String nickname;

        // data integrity
        {
            var nicknameRaw = event.getOption("nickname");

            if (nicknameRaw == null || nicknameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "whitelist/add",
                                "arg", "nickname")
                );
                return;
            }
            nickname = nicknameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelistManager.addToWhitelist(nickname)) {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().whitelist.notAddedNickname,
                            "nickname", nickname
                    )
            );
        } else {
            discordManager.sendSuccess(event,
                    MessageFormatter.format(
                            lang().whitelist.addedNickname,
                            "nickname", nickname
                    )
            );
        }
    }

    @SlashCommand(path = "whitelist/remove", deferReply = true, deferEphemeral = true)
    public void whitelistRemoveCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || event.getGuild() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        if (!general().whitelist.enable) {
            discordManager.sendError(event,
                    lang().whitelist.disabled
            );
            return;
        }

        String nickname;

        // data integrity
        {
            var nicknameRaw = event.getOption("nickname");

            if (nicknameRaw == null || nicknameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "whitelist/remove",
                                "arg", "nickname")
                );
                return;
            }
            nickname = nicknameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelistManager.removeFromWhitelist(nickname)) {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().whitelist.notRemovedNickname,
                            "nickname", nickname
                    )
            );
        } else {
            discordManager.sendSuccess(event,
                    MessageFormatter.format(
                            lang().whitelist.removedNickname,
                            "nickname", nickname
                    )
            );

            // server kick
            Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
                var player = Bukkit.getPlayer(nickname);
                if (player != null) {
                    player.kick(lang().remove.kickMessage);
                }
            });
        }
    }
}
