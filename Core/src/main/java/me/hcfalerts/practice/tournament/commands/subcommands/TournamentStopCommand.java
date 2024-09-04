package me.hcfalerts.practice.tournament.commands.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.tournament.Tournament;
import me.hcfalerts.practice.tournament.TournamentState;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TournamentStopCommand extends BaseCommand {

    @Command(name = "tournament.stop", permission = "practice.command.tournament.stop")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Tournament<?> tournament = Tournament.getTournament();
        if (tournament == null || tournament.getState() == TournamentState.ENDED) {
            player.sendMessage(ChatColor.RED + "No tournament found.");
            new MessageFormat(Locale.TOURNAMENT_NO_FOUND
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }
        if ((tournament.getState() == TournamentState.IN_FIGHT || tournament.getState() == TournamentState.SELECTING_DUELS)
                && tournament.getTeams().size() == 1) {
            tournament.end(tournament.getTeams().get(0));
            return;
        }
        tournament.end(null);
    }
}