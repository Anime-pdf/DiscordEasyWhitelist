package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.config.ConfigContainer;
import me.animepdf.dew.util.DiscordUtils;
import me.animepdf.dew.util.MessageFormatter;
import me.animepdf.dew.util.WhitelistUtils;
import net.aniby.simplewhitelist.PaperWhitelistPlugin;
import net.aniby.simplewhitelist.configuration.Whitelist;
import org.bukkit.Bukkit;

import java.util.Set;

public class WhitelistCommands implements SlashCommandProvider {
    private final ConfigContainer configContainer;
    private final PaperWhitelistPlugin whitelistPlugin;
    private final Whitelist whitelist;
    private final DiscordEasyWhitelist plugin;

    public WhitelistCommands(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
        this.whitelistPlugin = plugin.getSimpleWhitelistHandler();
        this.whitelist = this.whitelistPlugin.getConfiguration().getWhitelist();
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("whitelist", "Accept a mimocrocodile to the server")
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
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        String username;

        // data integrity
        {
            var usernameRaw = event.getOption("username");

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                event.getHook().sendMessage(
                        this.configContainer.getLanguageConfig().errorPrefix +
                                MessageFormatter.create()
                                        .set("command", "whitelist/check")
                                        .set("arg", "username")
                                        .apply(this.configContainer.getLanguageConfig().wrongCommandArgument)
                ).queue();
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelist.contains(username)) {
            event.getHook().sendMessage(MessageFormatter.create()
                    .set("username", username)
                    .apply(this.configContainer.getLanguageConfig().whitelistContainsUsername)
            ).queue();
        } else {
            event.getHook().sendMessage(MessageFormatter.create()
                    .set("username", username)
                    .apply(this.configContainer.getLanguageConfig().whitelistNotContainsUsername)
            ).queue();
        }
    }

    @SlashCommand(path = "whitelist/add", deferReply = true, deferEphemeral = true)
    public void whitelistAddCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        String username;

        // data integrity
        {
            var usernameRaw = event.getOption("username");

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                event.getHook().sendMessage(
                        this.configContainer.getLanguageConfig().errorPrefix +
                                MessageFormatter.create()
                                        .set("command", "whitelist/add")
                                        .set("arg", "username")
                                        .apply(this.configContainer.getLanguageConfig().wrongCommandArgument)
                ).queue();
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // whitelist
        if (whitelist.contains(username)) {
            event.getHook().sendMessage(MessageFormatter.create()
                    .set("username", username)
                    .apply(this.configContainer.getLanguageConfig().whitelistAlreadyContainsUsername)
            ).queue();
        } else {
            WhitelistUtils.addToWhitelist(this.plugin.getLogger(), this.whitelistPlugin, username);
            event.getHook().sendMessage(MessageFormatter.create()
                    .set("username", username)
                    .apply(this.configContainer.getLanguageConfig().whitelistAddedUsername)
            ).queue();
        }
    }

    @SlashCommand(path = "whitelist/remove", deferReply = true, deferEphemeral = true)
    public void whitelistRemoveCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        String username;

        // data integrity
        {
            var usernameRaw = event.getOption("username");

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                event.getHook().sendMessage(
                        this.configContainer.getLanguageConfig().errorPrefix +
                                MessageFormatter.create()
                                        .set("command", "whitelist/remove")
                                        .set("arg", "member")
                                        .apply(this.configContainer.getLanguageConfig().wrongCommandArgument)
                ).queue();
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // whitelist
        if (!this.whitelist.contains(username)) {
            event.getHook().sendMessage(MessageFormatter.create()
                    .set("username", username)
                    .apply(this.configContainer.getLanguageConfig().whitelistNotContainsUsername)
            ).queue();
        } else {
            WhitelistUtils.removeFromWhitelist(this.plugin.getLogger(), this.whitelistPlugin, username);

            // server kick
            Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
                var player = Bukkit.getPlayer(username);
                if (player != null) {
                    player.kick(this.configContainer.getLanguageConfig().kickMessage);
                }
            });

            event.getHook().sendMessage(MessageFormatter.create()
                    .set("username", username)
                    .apply(this.configContainer.getLanguageConfig().whitelistRemovedUsername)
            ).queue();
        }
    }
}
