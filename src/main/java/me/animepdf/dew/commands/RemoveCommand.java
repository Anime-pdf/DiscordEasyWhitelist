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
import me.animepdf.dew.util.DiscordUtils;
import me.animepdf.dew.util.LinkUtils;
import me.animepdf.dew.util.MessageFormatter;
import me.animepdf.dew.util.WhitelistUtils;
import net.aniby.simplewhitelist.PaperWhitelistPlugin;
import net.aniby.simplewhitelist.configuration.Whitelist;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RemoveCommand implements SlashCommandProvider {
    private final ConfigContainer configContainer;
    private final PaperWhitelistPlugin whitelistPlugin;
    private final Whitelist whitelist;
    private final DiscordEasyWhitelist plugin;

    public RemoveCommand(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
        this.whitelistPlugin = plugin.getSimpleWhitelistHandler();
        this.whitelist = this.whitelistPlugin.getConfiguration().getWhitelist();
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("remove", "Remove player from the game server and discord server")
                        .addOption(OptionType.USER, "member", "Member to remove", true)
                        .addOption(OptionType.STRING, "username", "Minecraft username, will be taken from guild nickname if empty", false)
                )
        );
    }

    @SlashCommand(path = "remove", deferReply = true, deferEphemeral = true)
    public void removeCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        boolean explicitUsername = false;
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
                                        .set("command", "remove")
                                        .set("arg", "member")
                                        .apply(this.configContainer.getLanguageConfig().wrongCommandArgument)
                ).queue();
                return;
            }
            member = memberRaw.getAsMember();

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                username = member.getEffectiveName();
            } else {
                explicitUsername = true;
                username = usernameRaw.getAsString().strip();
            }
        }

        List<String> output = new ArrayList<>();

        // discord
        if (configContainer.getGeneralConfig().enableLinking) {
            var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
            var linkedUuid = linkManager.getUuid(member.getId());
            if (linkedUuid == null) {
                output.add(this.configContainer.getLanguageConfig().warningPrefix +
                        MessageFormatter.create()
                                .set("discord_mention", member.getAsMention())
                                .set("discord_username", member.getUser().getAsTag())
                                .set("discord_name", member.getEffectiveName())
                                .set("discord_id", member.getId())
                                .apply(this.configContainer.getLanguageConfig().discordNotLinked));
            } else {
                if(this.configContainer.getGeneralConfig().useLinkedUsernameInRemove && !explicitUsername) {
                    username = Bukkit.getOfflinePlayer(linkedUuid).getName();
                    if (username == null) {
                        username = member.getEffectiveName();
                    }
                }
                LinkUtils.unlinkAccount(this.plugin.getLogger(), member.getId());
            }
        }

        // whitelist
        if (!whitelist.contains(username)) {
            output.add(this.configContainer.getLanguageConfig().warningPrefix +
                    MessageFormatter.create()
                            .set("username", username)
                            .apply(this.configContainer.getLanguageConfig().whitelistNotContainsUsername));
        } else {
            WhitelistUtils.removeFromWhitelist(this.plugin.getLogger(), this.whitelistPlugin, username);
        }

        // server kick
        final String tempUser = username;
        Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
            var player = Bukkit.getPlayer(tempUser);
            if (player != null) {
                player.kick(Component.text("You was removed from the whitelist"));
            }
        });

        // ban message
        var guild = member.getGuild();
        var banChannel = guild.getTextChannelById(this.configContainer.getGeneralConfig().bansChannelId);
        if (banChannel == null) {
            output.add(this.configContainer.getLanguageConfig().warningPrefix +
                    MessageFormatter.create()
                            .set("channel_id", this.configContainer.getGeneralConfig().bansChannelId)
                            .apply(this.configContainer.getLanguageConfig().channelNotFound));
        } else {
            String message = String.join("\n", this.configContainer.getLanguageConfig().banMessage);
            banChannel.sendMessage(
                    MessageFormatter.create()
                            .set("discord_mention", member.getAsMention())
                            .set("discord_username", member.getUser().getAsTag())
                            .set("discord_name", member.getEffectiveName())
                            .set("discord_id", member.getId())
                            .set("username", username)
                            .apply(message)
            ).queue();
        }

        // direct message
        member.getUser().openPrivateChannel().flatMap(
                privateChannel ->
                        privateChannel.sendMessage(String.join("\n", this.configContainer.getLanguageConfig().banDirectMessage)))
                .queue();

        // ban
        guild.ban(member, 0, this.configContainer.getLanguageConfig().discordBanReason).queue();

        // success
        MessageFormatter formatter = MessageFormatter.create()
                .set("discord_mention", member.getAsMention())
                .set("discord_username", member.getUser().getAsTag())
                .set("discord_name", member.getEffectiveName())
                .set("discord_id", member.getId())
                .set("username", username);

        if (output.isEmpty()) {
            event.getHook().sendMessage(
                    formatter.apply(this.configContainer.getLanguageConfig().successPrefix + this.configContainer.getLanguageConfig().removeOutputWithoutWarnings)
            ).queue();
        } else {
            event.getHook().sendMessage(
                    formatter.set("output", String.join("\n", output)).apply(this.configContainer.getLanguageConfig().outputWithWarnings) +
                            formatter.apply(this.configContainer.getLanguageConfig().successPrefix + this.configContainer.getLanguageConfig().removeOutputWithoutWarnings)
            ).queue();
        }
    }
}
