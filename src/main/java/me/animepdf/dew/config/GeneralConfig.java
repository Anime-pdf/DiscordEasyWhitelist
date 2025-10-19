package me.animepdf.dew.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class GeneralConfig {
    public boolean enableLinking = false;
    public List<String> moderatorRoleIds = List.of("0000000000000000000", "0000000000000000000");

    @ConfigSerializable
    public static class AcceptOptions {
        public boolean addToWhitelist = true;
        public boolean changeGuildName = true;
        public boolean addRoles = true;
        public List<String> rolesToAdd = List.of("0000000000000000000", "0000000000000000000");
        public boolean removeRoles = true;
        public List<String> rolesToRemove = List.of("0000000000000000000", "0000000000000000000");
        public boolean sendWelcomeMessage = true;
        public String welcomeChannelId = "0000000000000000000";

        public AcceptOptions() {
        }
    }
    public AcceptOptions accept = new AcceptOptions();

    @ConfigSerializable
    public static class RemoveOptions {
        public boolean fallbackToUsernameOnRemove = false;
        public boolean removeFromWhitelist = true;
        public boolean kickFromServerIfOnline = true;
        public boolean sendBanMessage = true;
        public String bansChannelId = "0000000000000000000";
        public boolean sendDirectBanMessage = true;
        public boolean banOnDiscordServer = true;

        public RemoveOptions() {
        }
    }
    public RemoveOptions remove = new RemoveOptions();

    public GeneralConfig() {
    }
}
