package me.animepdf.dew.commands;

import github.scarsz.discordsrv.api.commands.PluginSlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommand;
import github.scarsz.discordsrv.api.commands.SlashCommandProvider;
import github.scarsz.discordsrv.dependencies.jda.api.entities.Member;
import github.scarsz.discordsrv.dependencies.jda.api.events.interaction.SlashCommandEvent;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.OptionType;
import github.scarsz.discordsrv.dependencies.jda.api.interactions.commands.build.CommandData;
import me.animepdf.dew.DiscordEasyWhitelist;
import me.animepdf.dew.abstact.DEWComponent;
import me.animepdf.dew.util.BukkitUtils;
import me.animepdf.dew.util.MessageFormatter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class StatusCommand extends DEWComponent implements SlashCommandProvider {
    public StatusCommand(DiscordEasyWhitelist plugin) {
        super(plugin);
    }

    @Override
    public Set<PluginSlashCommand> getSlashCommands() {
        return Set.of(
                new PluginSlashCommand(this.plugin, new CommandData("status", "List all players online"))
        );
    }

    @SlashCommand(path = "status", deferReply = true, deferEphemeral = true)
    public void statusCommand(SlashCommandEvent event) {
        if (!general().status.enable) {
            discordManager.sendError(event,
                    lang().status.disabled
            );
            return;
        }

        var players = Bukkit.getOnlinePlayers();
        if (players.isEmpty()) {
            discordManager.send(event,
                    lang().status.statusMessageNoPlayers
            );
            return;
        }

        List<String> playerEntries = new ArrayList<>();

        for (Player player : players) {
            var linkedDiscord = linkManager.getLinkedDiscord(player.getName());
            var user = discordManager.getUser(linkedDiscord);
            if (user == null) {
                playerEntries.add(
                        MessageFormatter.format(
                                lang().status.statusEntry,
                                "username", player.getName()
                        )
                );
            } else {
                playerEntries.add(
                        MessageFormatter.format(
                                lang().status.statusEntryLinked,
                                "discord_mention", user.getAsMention(),
                                "discord_username", user.getAsTag(),
                                "discord_name", user.getEffectiveName(),
                                "discord_id", user.getId(),
                                "username", player.getName()
                        )
                );
            }
        }

        // success
        event.getHook().sendMessage(
                MessageFormatter.format(
                        lang().status.statusMessage,
                        "players_count", players.size(),
                        "players", String.join("\n", playerEntries)
                )
        ).queue();
    }
}
