package me.animepdf.dew.util;

import net.aniby.simplewhitelist.PaperWhitelistPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class WhitelistUtils {
    public static void addToWhitelist(@NotNull Logger logger, @NotNull PaperWhitelistPlugin whitelist, @NotNull String username) {
        if (whitelist.getConfiguration().getWhitelist().contains(username))
            return;
        whitelist.getConfiguration().getWhitelist().add(username);
        whitelist.getConfiguration().saveWhitelist();
        logger.info(String.format("'%s' was added to the whitelist", username));
    }

    public static void removeFromWhitelist(@NotNull Logger logger, @NotNull PaperWhitelistPlugin whitelist, @NotNull String username) {
        if (!whitelist.getConfiguration().getWhitelist().contains(username))
            return;
        whitelist.getConfiguration().getWhitelist().remove(username);
        whitelist.getConfiguration().saveWhitelist();
        logger.info(String.format("'%s' was removed from the whitelist", username));
    }
}
