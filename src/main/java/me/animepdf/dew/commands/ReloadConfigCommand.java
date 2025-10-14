package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.config.ConfigContainer;
import me.animepdf.dew.util.DiscordUtils;

import java.util.Set;

public class ReloadConfigCommand implements SlashCommandProvider {
    private final ConfigContainer configContainer;
    private final DiscordEasyWhitelist plugin;

    public ReloadConfigCommand(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("reload", "Reload config"))
        );
    }

    @SlashCommand(path = "reload", deferEphemeral = true, deferReply = true)
    public void reloadCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        this.configContainer.reloadConfigs();
        event.getHook().sendMessage(this.configContainer.getLanguageConfig().successPrefix + this.configContainer.getLanguageConfig().configReloaded).queue();
    }
}
