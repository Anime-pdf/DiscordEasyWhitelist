package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.abstact.DEWComponent;

import java.util.Set;

public class ReloadConfigCommand extends DEWComponent implements SlashCommandProvider {
    public ReloadConfigCommand(DiscordEasyWhitelist plugin) {
        super(plugin);
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
        if (event.getMember() == null || !discordManager.hasModPermission(event.getMember())) {
            discordManager.sendError(event, lang().general.noPermission);
            return;
        }

        this.configContainer.reloadConfigs();
        discordManager.sendSuccess(event, lang().reload.reloaded);
    }
}
