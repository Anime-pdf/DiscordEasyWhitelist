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
                        .addSubcommands(new SubcommandData("check", "Check if username is in whitelist")
                                .addOption(OptionType.STRING, "username", "Username to check", true)
                        )
                        .addSubcommands(new SubcommandData("add", "Add username to the whitelist")
                                .addOption(OptionType.STRING, "username", "Minecraft username to add to whitelist", true)
                        )
                        .addSubcommands(new SubcommandData("remove", "Remove username from the whitelist")
                                .addOption(OptionType.STRING, "username", "Minecraft username to remove from whitelist", true)
                        )
                )
        );
    }

    @SlashCommand(path = "whitelist/check", deferReply = true, deferEphemeral = true)
    public void whitelistCheckCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        String username;

        // data integrity
        {
            var usernameRaw = event.getOption("username");

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "whitelist/check",
                                "arg", "username")
                );
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelistManager.isInWhitelist(username)) {
            discordManager.sendSuccess(event,
                    MessageFormatter.format(
                            lang().whitelist.containsUsername,
                            "username", username
                    )
            );
        } else {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().whitelist.notContainsUsername,
                            "username", username
                    )
            );
        }
    }

    @SlashCommand(path = "whitelist/add", deferReply = true, deferEphemeral = true)
    public void whitelistAddCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        String username;

        // data integrity
        {
            var usernameRaw = event.getOption("username");

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "whitelist/add",
                                "arg", "username")
                );
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelistManager.addToWhitelist(username)) {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().whitelist.notAddedUsername,
                            "username", username
                    )
            );
        } else {
            discordManager.sendSuccess(event,
                    MessageFormatter.format(
                            lang().whitelist.addedUsername,
                            "username", username
                    )
            );
        }
    }

    @SlashCommand(path = "whitelist/remove", deferReply = true, deferEphemeral = true)
    public void whitelistRemoveCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        String username;

        // data integrity
        {
            var usernameRaw = event.getOption("username");

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                discordManager.sendError(event,
                        MessageFormatter.format(
                                lang().general.wrongCommandArgument,
                                "command", "whitelist/remove",
                                "arg", "username")
                );
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelistManager.removeFromWhitelist(username)) {
            discordManager.sendError(event,
                    MessageFormatter.format(
                            lang().whitelist.notRemovedUsername,
                            "username", username
                    )
            );
        } else {
            discordManager.sendSuccess(event,
                    MessageFormatter.format(
                            lang().whitelist.removedUsername,
                            "username", username
                    )
            );

            // server kick
            Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
                var player = Bukkit.getPlayer(username);
                if (player != null) {
                    player.kick(lang().remove.kickMessage);
                }
            });
        }
    }
}
