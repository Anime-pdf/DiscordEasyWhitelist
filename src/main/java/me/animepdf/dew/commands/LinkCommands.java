package me.animepdf.dew.commands;

import github.scarsz.discordsrv.DiscordSRV;
import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.OptionData;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.SubcommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.config.ConfigContainer;
import me.animepdf.dew.util.BukkitUtils;
import me.animepdf.dew.util.DiscordUtils;
import me.animepdf.dew.util.LinkUtils;
import me.animepdf.dew.util.MessageFormatter;
import org.bukkit.Bukkit;

import java.util.Set;
import java.util.UUID;

public class LinkCommands implements SlashCommandProvider {
    private final ConfigContainer configContainer;
    private final DiscordEasyWhitelist plugin;

    public LinkCommands(DiscordEasyWhitelist plugin) {
        this.plugin = plugin;
        this.configContainer = plugin.getConfigContainer();
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("link", "Remove player from the game server and discord server")
                        .addSubcommands(new SubcommandData("check", "Check if account/username is linked")
                                .addOptions(new OptionData(OptionType.USER, "member", "Member to check", false))
                                .addOptions(new OptionData(OptionType.STRING, "username", "Username to check", false))
                        )
                        .addSubcommands(new SubcommandData("add", "Link account to username")
                                .addOptions(new OptionData(OptionType.USER, "member", "Member to link", true))
                                .addOptions(new OptionData(OptionType.STRING, "username", "Username to link", true))
                        )
                        .addSubcommands(new SubcommandData("remove", "Link account to username")
                                .addOptions(new OptionData(OptionType.USER, "member", "Member to link", false))
                                .addOptions(new OptionData(OptionType.STRING, "username", "Username to link", false))
                        )
                )
        );
    }

    @SlashCommand(path = "link/check", deferReply = true, deferEphemeral = true)
    public void linkCheckCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        Member member = null;
        String username = null;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw != null && memberRaw.getAsMember() != null) {
                member = memberRaw.getAsMember();
            }
            if (usernameRaw != null && !usernameRaw.getAsString().isEmpty()) {
                username = usernameRaw.getAsString().strip();
            }

            if (username == null && member == null) {
                event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noCommandArgument).queue();
                return;
            }
        }

        // discord
        var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();

        if (member == null) {
            UUID uuid = BukkitUtils.uuidFromUsername(username);
            String discordId = linkManager.getDiscordId(uuid);
            var user = discordId == null ? null : DiscordSRV.getPlugin().getJda().getUserById(discordId);
            if (discordId != null) {
                event.getHook().sendMessage(MessageFormatter.create()
                        .set("username", username)
                        .set("discord_mention", String.format("<@%s>", discordId))
                        .set("discord_username", user == null ? "" : user.getAsTag())
                        .set("discord_name", user == null ? "" : user.getEffectiveName())
                        .set("discord_id", discordId)
                        .apply(this.configContainer.getLanguageConfig().usernameLinked)
                ).queue();
            } else {
                event.getHook().sendMessage(MessageFormatter.create()
                        .set("username", username)
                        .apply(this.configContainer.getLanguageConfig().usernameNotLinked)
                ).queue();
            }
        } else if (username == null) {
            String discordId = member.getId();
            UUID uuid = linkManager.getUuid(discordId);
            if (uuid != null) {
                event.getHook().sendMessage(MessageFormatter.create()
                        .set("username", Bukkit.getOfflinePlayer(uuid).getName())
                        .set("discord_mention", member.getAsMention())
                        .set("discord_username", member.getUser().getAsTag())
                        .set("discord_name", member.getEffectiveName())
                        .set("discord_id", discordId)
                        .apply(this.configContainer.getLanguageConfig().discordLinked)
                ).queue();
            } else {
                event.getHook().sendMessage(MessageFormatter.create()
                        .set("discord_mention", member.getAsMention())
                        .set("discord_username", member.getUser().getAsTag())
                        .set("discord_name", member.getEffectiveName())
                        .set("discord_id", discordId)
                        .apply(this.configContainer.getLanguageConfig().discordNotLinked)
                ).queue();
            }
        } else {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().tooManyCommandArguments).queue();
        }
    }

    @SlashCommand(path = "link/add", deferReply = true, deferEphemeral = true)
    public void linkAddCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        Member member = null;
        String username = null;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw == null || memberRaw.getAsMember() == null) {
                event.getHook().sendMessage(
                        this.configContainer.getLanguageConfig().errorPrefix +
                                MessageFormatter.create()
                                        .set("command", "link/check")
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
                                        .set("command", "link/check")
                                        .set("arg", "username")
                                        .apply(this.configContainer.getLanguageConfig().wrongCommandArgument)
                ).queue();
                return;
            }
            username = usernameRaw.getAsString().strip();
        }

        // discord
        var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        String discordId = member.getId();
        UUID uuid = BukkitUtils.uuidFromUsername(username);
        UUID linkedToDiscordId = linkManager.getUuid(discordId);
        if (linkedToDiscordId == uuid) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix +
                    MessageFormatter.create()
                            .set("discord_mention", member.getAsMention())
                            .set("discord_username", member.getUser().getAsTag())
                            .set("discord_name", member.getEffectiveName())
                            .set("discord_id", member.getId())
                            .set("username", Bukkit.getOfflinePlayer(linkedToDiscordId).getName())
                            .apply(this.configContainer.getLanguageConfig().discordAlreadyLinked)
            ).queue();
            return;
        } else if (linkManager.getUuid(discordId) != null || linkManager.getDiscordId(uuid) != null) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().discordOrUsernameAlreadyLinked).queue();
            return;
        }

        LinkUtils.linkAccount(this.plugin.getLogger(), uuid, discordId);
        event.getHook().sendMessage(this.configContainer.getLanguageConfig().successPrefix +
                MessageFormatter.create()
                        .set("discord_mention", member.getAsMention())
                        .set("discord_username", member.getUser().getAsTag())
                        .set("discord_name", member.getEffectiveName())
                        .set("discord_id", member.getId())
                        .set("username", username)
                        .apply(this.configContainer.getLanguageConfig().discordLinked)
        ).queue();
    }

    @SlashCommand(path = "link/remove", deferReply = true, deferEphemeral = true)
    public void linkRemoveCommand(SlashCommandEvent event) {
        // permission
        if (event.getMember() == null || !DiscordUtils.hasModPermission(this.configContainer, event.getMember())) {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noPermission).queue();
            return;
        }

        Member member = null;
        String username = null;

        // data integrity
        {
            var memberRaw = event.getOption("member");
            var usernameRaw = event.getOption("username");

            if (memberRaw != null && memberRaw.getAsMember() != null) {
                member = memberRaw.getAsMember();
            }
            if (usernameRaw != null && !usernameRaw.getAsString().isEmpty()) {
                username = usernameRaw.getAsString().strip();
            }

            if (username == null && member == null) {
                event.getHook().sendMessage(this.configContainer.getLanguageConfig().errorPrefix + this.configContainer.getLanguageConfig().noCommandArgument).queue();
                return;
            }
        }

        // discord
        var linkManager = DiscordSRV.getPlugin().getAccountLinkManager();
        if (member == null) {
            UUID uuid = BukkitUtils.uuidFromUsername(username);
            String discordId = linkManager.getDiscordId(uuid);
            if (discordId == null) {
                event.getHook().sendMessage(MessageFormatter.create()
                        .set("username", username)
                        .apply(this.configContainer.getLanguageConfig().usernameNotLinked)
                ).queue();
                return;
            }
            LinkUtils.unlinkAccount(this.plugin.getLogger(), uuid);
            event.getHook().sendMessage(MessageFormatter.create()
                    .set("username", username)
                    .apply(this.configContainer.getLanguageConfig().usernameUnLinked)
            ).queue();
        } else if (username == null) {
            String discordId = member.getId();
            UUID uuid = linkManager.getUuid(discordId);
            if (uuid == null) {
                event.getHook().sendMessage(MessageFormatter.create()
                        .set("discord_mention", member.getAsMention())
                        .set("discord_username", member.getUser().getAsTag())
                        .set("discord_name", member.getEffectiveName())
                        .set("discord_id", discordId)
                        .apply(this.configContainer.getLanguageConfig().discordNotLinked)
                ).queue();
                return;
            }
            LinkUtils.unlinkAccount(this.plugin.getLogger(), discordId);
            event.getHook().sendMessage(MessageFormatter.create()
                    .set("discord_mention", member.getAsMention())
                    .set("discord_username", member.getUser().getAsTag())
                    .set("discord_name", member.getEffectiveName())
                    .set("discord_id", discordId)
                    .apply(this.configContainer.getLanguageConfig().discordUnLinked)
            ).queue();
        } else {
            event.getHook().sendMessage(this.configContainer.getLanguageConfig().tooManyCommandArguments).queue();
        }
    }
}
