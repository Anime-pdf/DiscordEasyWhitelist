package me.animepdf.dew;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import lombok.Getter;
import me.animepdf.dew.commands.*;
import me.animepdf.dew.config.ConfigContainer;
import net.aniby.simplewhitelist.PaperWhitelistPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class DiscordEasyWhitelist extends JavaPlugin implements Listener {
    @Getter
    private PaperWhitelistPlugin simpleWhitelistHandler;
    @Getter
    private ConfigContainer configContainer;

    @Override
    public void onEnable() {
        this.configContainer = new ConfigContainer(getDataFolder());
        this.configContainer.loadConfigs();

        Plugin whitelistPlugin = Bukkit.getPluginManager().getPlugin("simplewhitelist");
        if ((whitelistPlugin instanceof PaperWhitelistPlugin typedPlugin) && whitelistPlugin.isEnabled()) {
            this.simpleWhitelistHandler = typedPlugin;
        } else {
            getLogger().severe("SimpleWhitelist is not found or disabled!");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        List<SlashCommandProvider> slashCommands = List.of(
                new AcceptCommand(this),
                new RemoveCommand(this),
                new WhitelistCommands(this),
                new LinkCommands(this),
                new ReloadConfigCommand(this)
        );
        for (SlashCommandProvider slashCommand : slashCommands) {
            DiscordSRV.api.addSlashCommandProvider(slashCommand);
        }
    }
}
