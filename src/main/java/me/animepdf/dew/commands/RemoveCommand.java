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
import me.animepdf.dew.config.LanguageConfig;
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
    private final LanguageConfig languageConfig;
    private final PaperWhitelistPlugin whitelistPlugin;
    private final Whitelist whitelist;
    private final DiscordEasyWhitelist plugin;

    public RemoveCommand(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
        this.languageConfig = this.configContainer.getLanguageConfig();
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
            event.getHook().sendMessage(this.languageConfig.errorPrefix + this.languageConfig.noPermission).queue();
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
                        this.languageConfig.errorPrefix +
                                MessageFormatter.create()
                                        .set("command", "remove")
                                        .set("arg", "member")
                                        .apply(this.languageConfig.wrongCommandArgument)
                ).queue();
                return;
            }
            member = memberRaw.getAsMember();

            if (usernameRaw == null || usernameRaw.getAsString().isEmpty()) {
                username = member.getEffectiveName();
            } else {
                username = usernameRaw.getAsString().strip();
            }
        }

        List<String> output = new ArrayList<>();

        // discord
        var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        if (linkManager.getUuid(member.getId()) == null) {
            output.add(this.languageConfig.warningPrefix +
                    MessageFormatter.create()
                            .set("discord_mention", member.getAsMention())
                            .set("discord_username", member.getUser().getAsTag())
                            .set("discord_name", member.getEffectiveName())
                            .set("discord_id", member.getId())
                            .apply(this.languageConfig.discordNotLinked));
        } else {
            LinkUtils.unlinkAccount(this.plugin.getLogger(), member.getId());
        }

        // whitelist
        if (!whitelist.contains(username)) {
            output.add(this.languageConfig.warningPrefix +
                    MessageFormatter.create()
                            .set("username", username)
                            .apply(this.languageConfig.whitelistNotContainsUsername));
        } else {
            WhitelistUtils.removeFromWhitelist(this.plugin.getLogger(), this.whitelistPlugin, username);
        }

        // server kick
        Bukkit.getScheduler().runTask(this.plugin, scheduledTask -> {
            var player = Bukkit.getPlayer(username);
            if (player != null) {
                player.kick(Component.text("You was removed from the whitelist"));
            }
        });

        // ban message
        var guild = member.getGuild();
        var banChannel = guild.getTextChannelById(this.configContainer.getGeneralConfig().bansChannelId);
        if (banChannel == null) {
            output.add(this.languageConfig.warningPrefix +
                    MessageFormatter.create()
                            .set("channel_id", this.configContainer.getGeneralConfig().bansChannelId)
                            .apply(this.languageConfig.channelNotFound));
        } else {
            String message = String.join("\n", this.configContainer.getLanguageConfig().banMessage);
            banChannel.sendMessage(
                    MessageFormatter.create()
                            .set("discord_mention", member.getAsMention())
                            .set("discord_username", member.getUser().getAsTag())
                            .set("discord_name", member.getEffectiveName())
                            .set("discord_id", member.getId())
                            .apply(message)
            ).queue();
        }

        // ban
        guild.ban(member, 0, this.languageConfig.discordBanReason).queue();

        // success
        MessageFormatter formatter = MessageFormatter.create()
                .set("discord_mention", member.getAsMention())
                .set("discord_username", member.getUser().getAsTag())
                .set("discord_name", member.getEffectiveName())
                .set("discord_id", member.getId())
                .set("username", username);

        if (output.isEmpty()) {
            event.getHook().sendMessage(
                    formatter.apply(this.languageConfig.successPrefix + this.languageConfig.removeOutputWithoutWarnings)
            ).queue();
        } else {
            event.getHook().sendMessage(
                    formatter.set("output", String.join("\n", output)).apply(this.languageConfig.outputWithWarnings) +
                            formatter.apply(this.languageConfig.successPrefix + this.languageConfig.removeOutputWithoutWarnings)
            ).queue();
        }
    }
}
