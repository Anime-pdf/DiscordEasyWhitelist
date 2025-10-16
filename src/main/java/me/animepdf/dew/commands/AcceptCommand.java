package me.animepdf.dew.commands;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.config.ConfigContainer;
import me.animepdf.dew.util.*;
import net.aniby.simplewhitelist.PaperWhitelistPlugin;
import net.aniby.simplewhitelist.configuration.Whitelist;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class AcceptCommand implements SlashCommandProvider {
    private final ConfigContainer configContainer;
    private final PaperWhitelistPlugin whitelistPlugin;
    private final Whitelist whitelist;
    private final DiscordEasyWhitelist plugin;

    public AcceptCommand(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
        this.whitelistPlugin = plugin.getSimpleWhitelistHandler();
        this.whitelist = this.whitelistPlugin.getConfiguration().getWhitelist();
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("accept", "Accept a mimocrocodile to the server")
                        .addOption(OptionType.USER, "member", "Member to add", true)
                        .addOption(OptionType.STRING, "username", "Minecraft username to add to whitelist", true)
                )
        );
    }

    @SlashCommand(path = "accept", deferReply = true, deferEphemeral = true)
    public void acceptCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        Member member;
        String username;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw == null || memberRaw.getAsMember() == null) {
                event.getHook().sendMessage(
                        this.configContainer.getLanguageConfig().errorPrefix +
                                MessageFormatter.create()
                                        .set("command", "accept")
                                        .set("arg", "member")
                                        .apply(this.configContainer.getLanguageConfig().wrongCommandArgument)
                ).queue();
                return;
            }
            member = memberRaw.getAsMember();

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                event.getHook().sendMessage(
                        this.configContainer.getLanguageConfig().errorPrefix +
                                MessageFormatter.create()
                                        .set("command", "accept")
                                        .set("arg", "username")
                                        .apply(this.configContainer.getLanguageConfig().wrongCommandArgument)
                ).queue();
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        List<String> output = new ArrayList<>();

        // discord
        if (configContainer.getGeneralConfig().enableLinking) {
            var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
            UUID uuid = BukkitUtils.uuidFromUsername(username);
            UUID linkedToDiscordId = linkManager.getUuid(member.getId());
            if (linkedToDiscordId != null) {
                output.add(this.configContainer.getLanguageConfig().warningPrefix +
                        MessageFormatter.create()
                                .set("discord_mention", member.getAsMention())
                                .set("discord_username", member.getUser().getAsTag())
                                .set("discord_name", member.getEffectiveName())
                                .set("discord_id", member.getId())
                                .set("username", Bukkit.getOfflinePlayer(linkedToDiscordId).getName())
                                .apply(this.configContainer.getLanguageConfig().discordAlreadyLinked));
            } else {
                LinkUtils.linkAccount(this.plugin.getLogger(), uuid, member.getId());
            }
        }

        // whitelist
        if (whitelist.contains(username)) {
            output.add(this.configContainer.getLanguageConfig().warningPrefix +
                    MessageFormatter.create()
                            .set("username", username)
                            .apply(this.configContainer.getLanguageConfig().whitelistAlreadyContainsUsername));
        } else {
            WhitelistUtils.addToWhitelist(this.plugin.getLogger(), this.whitelistPlugin, username);
        }

        // guild name
        member.modifyNickname(username).queue();

        // role stuff
        var guild = member.getGuild();
        for (String roleId : this.configContainer.getGeneralConfig().rolesToRemove) {
            var role = guild.getRoleById(roleId);
            if (role == null) {
                continue;
            }
            guild.removeRoleFromMember(member, role).queue();
        }
        for (String roleId : this.configContainer.getGeneralConfig().rolesToAdd) {
            var role = guild.getRoleById(roleId);
            if (role == null) {
                continue;
            }
            guild.addRoleToMember(member, role).queue();
        }

        // welcome message
        if (this.configContainer.getGeneralConfig().sendWelcomeMessage) {
            var welcomeChannel = guild.getTextChannelById(this.configContainer.getGeneralConfig().welcomeChannelId);
            if (welcomeChannel == null) {
                output.add(this.configContainer.getLanguageConfig().warningPrefix +
                        MessageFormatter.create()
                                .set("channel_id", this.configContainer.getGeneralConfig().welcomeChannelId)
                                .apply(this.configContainer.getLanguageConfig().channelNotFound));
            } else {
                String message = String.join("\n", this.configContainer.getLanguageConfig().welcomeMessage);
                welcomeChannel.sendMessage(
                        MessageFormatter.create()
                                .set("discord_mention", member.getAsMention())
                                .set("discord_username", member.getUser().getAsTag())
                                .set("discord_name", member.getEffectiveName())
                                .set("discord_id", member.getId())
                                .apply(message)
                ).queue();
            }
        }

        // success
        MessageFormatter formatter = MessageFormatter.create()
                .set("discord_mention", member.getAsMention())
                .set("discord_username", member.getUser().getAsTag())
                .set("discord_name", member.getEffectiveName())
                .set("discord_id", member.getId())
                .set("username", username);

        if (output.isEmpty()) {
            event.getHook().sendMessage(
                    formatter.apply(this.configContainer.getLanguageConfig().successPrefix + this.configContainer.getLanguageConfig().acceptOutputWithoutWarnings)
            ).queue();
        } else {
            event.getHook().sendMessage(
                    formatter.set("output", String.join("\n", output)).apply(this.configContainer.getLanguageConfig().outputWithWarnings) +
                            formatter.apply(this.configContainer.getLanguageConfig().successPrefix + this.configContainer.getLanguageConfig().acceptOutputWithoutWarnings)
            ).queue();
        }
    }
}
