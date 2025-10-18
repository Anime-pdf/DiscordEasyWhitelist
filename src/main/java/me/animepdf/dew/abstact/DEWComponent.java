package me.animepdf.dew.abstact;

import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.config.ConfigContainer;
import me.animepdf.dew.config.GeneralConfig;
import me.animepdf.dew.config.LanguageConfig;
import me.animepdf.dew.managers.DiscordManager;
import me.animepdf.dew.managers.LinkManager;
import me.animepdf.dew.managers.WhitelistManager;

public abstract class DEWComponent {
    protected final DiscordEasyWhitelist plugin;
    protected final ConfigContainer configContainer;
    protected final WhitelistManager whitelistManager;
    protected final LinkManager linkManager;
    protected final DiscordManager discordManager;

    public DEWComponent(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
        this.whitelistManager = plugin.getWhitelistManager();
        this.linkManager = plugin.getLinkManager();
        this.discordManager = plugin.getDiscordManager();
    }

    protected LanguageConfig lang() {
        return this.configContainer.getLanguageConfig();
    }

    protected GeneralConfig general() {
        return this.configContainer.getGeneralConfig();
    }
}
