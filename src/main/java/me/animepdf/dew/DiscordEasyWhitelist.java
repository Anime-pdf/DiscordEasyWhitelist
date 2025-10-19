package me.animepdf.dew;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.Subscribe;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.api.events.DiscordReadyEvent;
import lombok.Getter;
import me.animepdf.dew.commands.*;
import me.animepdf.dew.config.ConfigContainer;
import me.animepdf.dew.listeners.DiscordListener;
import me.animepdf.dew.managers.DiscordManager;
import me.animepdf.dew.managers.LinkManager;
import me.animepdf.dew.managers.WhitelistManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@Getter
public class DiscordEasyWhitelist extends JavaPlugin implements Listener {
    private ConfigContainer configContainer;
    private WhitelistManager whitelistManager;
    private LinkManager linkManager;
    private DiscordManager discordManager;

    @Override
    public void onEnable() {
        this.configContainer = new ConfigContainer(getDataFolder());
        this.configContainer.loadConfigs();

        this.whitelistManager = new WhitelistManager(this);
        this.linkManager = new LinkManager(this);
        this.discordManager = new DiscordManager(this);

        DiscordSRV.api.subscribe(this);

        List<SlashCommandProvider> slashCommands = List.of(
                new AcceptCommand(this),
                new RemoveCommand(this),
                new WhitelistCommands(this),
                new LinkCommands(this),
                new ReloadConfigCommand(this),
                new StatusCommand(this)
        );
        for (SlashCommandProvider slashCommand : slashCommands) {
            DiscordSRV.api.addSlashCommandProvider(slashCommand);
        }
    }

    @Subscribe
    public void discordReadyEvent(DiscordReadyEvent event) {
        DiscordSRV.getPlugin().getJda().addEventListener(new DiscordListener(this));
    }
}
