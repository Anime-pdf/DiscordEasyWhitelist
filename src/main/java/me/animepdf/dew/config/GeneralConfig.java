package me.animepdf.dew.config;

import lombok.Getter;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
@Getter
public class GeneralConfig {
    public boolean enableLinking = false;
    public boolean useLinkedUsernameInRemove = false;

    public boolean sendWelcomeMessage = true;
    public boolean sendBanMessage = true;
    public boolean sendDirectBanMessage = true;

    public List<String> moderatorRoleIds = List.of("0000000000000000000", "0000000000000000000");

    public List<String> rolesToRemove = List.of("0000000000000000000", "0000000000000000000");
    public List<String> rolesToAdd = List.of("0000000000000000000", "0000000000000000000");

    public String welcomeChannelId = "0000000000000000000";
    public String bansChannelId = "0000000000000000000";

    public GeneralConfig() {
    }
}
