package me.animepdf.dew.managers;

import me.animepdf.dew.DiscordEasyWhitelist;
import net.aniby.simplewhitelist.PaperWhitelistPlugin;
import net.aniby.simplewhitelist.configuration.Whitelist;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Logger;

public class WhitelistManager {
    private final DiscordEasyWhitelist plugin;
    private final Logger logger;
    private final PaperWhitelistPlugin whitelistPlugin;
    private final Whitelist whitelist;

    public WhitelistManager(@NotNull DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.logger = this.plugin.getLogger();

        Plugin whitelistPluginRaw = Bukkit.getPluginManager().getPlugin("simplewhitelist");
        if ((whitelistPluginRaw instanceof PaperWhitelistPlugin typedPlugin) && whitelistPluginRaw.isEnabled()) {
            this.whitelistPlugin = typedPlugin;
            this.whitelist = this.whitelistPlugin.getConfiguration().getWhitelist();
        } else {
            plugin.getLogger().severe("SimpleWhitelist is not found or disabled!");
            Bukkit.getPluginManager().disablePlugin(this.plugin);
            this.whitelistPlugin = null;
            this.whitelist = null;
        }
    }

    public boolean isInWhitelist(@Nullable String username) {
        if(username == null)
            return false;
        return this.whitelist.contains(username);
    }

    public boolean addToWhitelist(@NotNull String username) {
        if (this.whitelist.contains(username))
            return true;

        this.whitelist.add(username);
        this.whitelistPlugin.getConfiguration().saveWhitelist();
        this.logger.info(String.format("'%s' was added to the whitelist", username));

        return false;
    }

    public boolean removeFromWhitelist(@NotNull String username) {
        if (!this.whitelist.contains(username))
            return true;

        this.whitelist.remove(username);
        this.whitelistPlugin.getConfiguration().saveWhitelist();
        this.logger.info(String.format("'%s' was removed from the whitelist", username));

        return false;
    }
}
