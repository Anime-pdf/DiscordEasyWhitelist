package me.animepdf.dew.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class GeneralConfig {
    public List<String> moderatorRoles = List.of("0000000000000000000", "0000000000000000000");

    @ConfigSerializable
    public static class WhitelistOptions {
        public boolean enable = true;

        public WhitelistOptions() {
        }
    }
    public WhitelistOptions whitelist = new WhitelistOptions();

    @ConfigSerializable
    public static class LinkOptions {
        public boolean enable = true;

        public LinkOptions() {
        }
    }
    public LinkOptions link = new LinkOptions();

    @ConfigSerializable
    public static class AcceptOptions {
        public boolean enable = true;
        public boolean linkToNickname = true;
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
        public boolean enable = true;
        public boolean unlinkFromNickname = true;
        public boolean fallbackToGuildUsername = false;
        public boolean removeFromWhitelist = true;
        public boolean kickFromGameIfOnline = true;
        public boolean sendRemoveMessage = true;
        public String removeChannelId = "0000000000000000000";
        public boolean sendDirectMessage = true;
        public boolean banFromGuild = true;

        public RemoveOptions() {
        }
    }
    public RemoveOptions remove = new RemoveOptions();

    @ConfigSerializable
    public static class LeaveOptions {
        public boolean enable = false;
        public boolean unlinkIfAvailable = true;
        public boolean removeFromWhitelist = true;
        public boolean kickFromGameIfOnline = true;
        public boolean banFromGuild = true;
        public boolean sendLeaveMessage = true;
        public String leaveChannelId = "0000000000000000000";
        public boolean sendDirectMessage = true;
        public boolean sendReportMessage = true;
        public String reportChannelId = "0000000000000000000";

        public LeaveOptions() {
        }
    }
    public LeaveOptions leave = new LeaveOptions();

    @ConfigSerializable
    public static class StatusOptions {
        public boolean enable = true;

        public StatusOptions() {
        }
    }
    public StatusOptions status = new StatusOptions();

    public GeneralConfig() {
    }
}
