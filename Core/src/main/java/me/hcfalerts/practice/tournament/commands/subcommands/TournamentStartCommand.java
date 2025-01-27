package me.hcfalerts.practice.tournament.commands.subcommands;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.tournament.Tournament;
import me.hcfalerts.practice.tournament.impl.TournamentClans;
import me.hcfalerts.practice.tournament.impl.TournamentSolo;
import me.hcfalerts.practice.tournament.impl.TournamentTeams;
import me.hcfalerts.practice.utilities.DiscordWebhook;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.chat.Clickable;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.awt.*;
import java.io.IOException;

public class TournamentStartCommand extends BaseCommand {

    @Command(name = "tournament.start", permission = "practice.command.tournament.start")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length < 3) {
            player.sendMessage(CC.RED + "Please usage /tournament start (kit) (size) (limit) (true/false) [clans]");
            player.sendMessage(CC.RED + " » Size: min players in team (if 1 the tournament starts in solo mode)");
            player.sendMessage(CC.RED + "  » /tournament start NoDebuff '2' 100 false");
            player.sendMessage(CC.RED + " » Limit: max players/teams in the tournament");
            player.sendMessage(CC.RED + "  » /tournament start NoDebuff 2 '100' false");
            player.sendMessage(CC.RED + " » Clan: if min players is 2 or more with true, the tournament starts only for clans");
            player.sendMessage(CC.RED + "  » /tournament start NoDebuff 2 100 'true'");
            return;
        }

        if (Tournament.getTournament() != null) {
            new MessageFormat(Locale.TOURNAMENT_ALREADY_CREATED.format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }

        Kit kit = Kit.getByName(args[0]);
        if (kit == null) {
            player.sendMessage(CC.RED + "This kit doesn't exist.");
            return;
        }

        int size;
        if (!StringUtils.isNumeric(args[1])) {
            player.sendMessage(CC.RED + "Use a valid integer.");
            return;
        }
        size = Integer.parseInt(args[1]);

        int limit;
        if (!StringUtils.isNumeric(args[2])) {
            player.sendMessage(CC.RED + "Use a valid integer.");
            return;
        }
        limit = Integer.parseInt(args[2]);

        boolean clans = false;
        if (args.length > 3) {
            try {
                clans = Boolean.parseBoolean(args[3]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String type = "Solo";
        Tournament tournament = new TournamentSolo();
        if (size > 1) {
            type = "Teams";
            tournament = new TournamentTeams();
        }
        if (clans) {
            type = "Clans";
            tournament = new TournamentClans();
        }
        tournament.setClans(clans);
        tournament.setSize(size);
        tournament.setLimit(limit);
        tournament.setKit(kit);
        Tournament.setTournament(tournament);

        if (HCFPractice.get().isWebHook()) {
            BasicConfigurationFile webhookConfig = HCFPractice.get().getDiscordWebhookConfig();
            try {
                DiscordWebhook webhook = HCFPractice.get().getDiscordWebhook();
                webhook.setUsername(webhookConfig.getString("TOURNAMENT.USERNAME"));
                webhook.setContent(webhookConfig.getString("TOURNAMENT.CONTENT")
                        .replace("{kit}", kit.getName())
                        .replace("{size}", String.valueOf(size))
                        .replace("{limit}", String.valueOf(limit))
                        .replace("{type}", type));
                webhook.setTts(webhookConfig.getBoolean("TOURNAMENT.TTS"));
                webhook.setAvatarUrl(webhookConfig.getString("TOURNAMENT.AVATAR_URL"));

                DiscordWebhook.EmbedObject embed = new DiscordWebhook.EmbedObject();
                embed.setTitle(webhookConfig.getString("TOURNAMENT.EMBED.TITLE"));
                try {
                    embed.setColor(Color.getColor(webhookConfig.getString("TOURNAMENT.EMBED.COLOR")));
                } catch (Exception e) {
                    throw new IOException("Invalid color on DiscordWebhook.");
                }
                embed.setDescription(webhookConfig.getString("TOURNAMENT.EMBED.DESCRIPTION")
                        .replace("{kit}", kit.getName())
                        .replace("{size}", String.valueOf(size))
                        .replace("{limit}", String.valueOf(limit))
                        .replace("{type}", type));
                embed.setUrl(webhookConfig.getString("TOURNAMENT.EMBED.URL"));
                embed.setImage(webhookConfig.getString("TOURNAMENT.EMBED.IMAGE"));
                embed.setFooter(webhookConfig.getString("TOURNAMENT.EMBED.FOOTER.TEXT"),
                        webhookConfig.getString("TOURNAMENT.EMBED.FOOTER.ICON_URL"));
                embed.setAuthor(webhookConfig.getString("TOURNAMENT.EMBED.AUTHOR.NAME"),
                        webhookConfig.getString("TOURNAMENT.EMBED.AUTHOR.URL"),
                        webhookConfig.getString("TOURNAMENT.EMBED.AUTHOR.ICON_URL"));
                embed.setThumbnail(webhookConfig.getString("TOURNAMENT.EMBED.THUMBNAIL"));
                for (String s : webhookConfig.getStringList("TOURNAMENT.EMBED.FIELDS")) {
                    embed.addField(s
                            .replace("{kit}", kit.getName())
                            .replace("{size}", String.valueOf(size))
                            .replace("{limit}", String.valueOf(limit))
                            .replace("{type}", type));
                }
                webhook.addEmbed(embed);

                webhook.execute();
            } catch (IOException e) {
                throw new IllegalArgumentException("Please check if your webhook url is valid or send your discord-webhook.yml in support discord");
            }
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.get(onlinePlayer.getUniqueId());
            Clickable clickable = new Clickable(Locale.TOURNAMENT_CLICKABLE_MESSAGE.getString(profile.getLocale()),
                    Locale.TOURNAMENT_CLICKABLE_HOVER.getString(profile.getLocale()), "/tournament join");
            for (String s : Locale.TOURNAMENT_START_MESSAGE.getStringList(profile.getLocale())) {
                if (s.contains("{clickable}")) {
                    onlinePlayer.spigot().sendMessage(clickable.asComponents());
                } else {
                    if (s.contains("{bars}")) s = s.replace("{bars}", CC.CHAT_BAR);

                    onlinePlayer.sendMessage(CC.translate(s));
                }
            }
        }
    }
}