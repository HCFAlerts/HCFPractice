package me.hcfalerts.practice.tournament;

import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.events.MatchEndEvent;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class TournamentListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        if (Tournament.getTournament() != null &&
                (Tournament.getTournament().getState() == TournamentState.STARTING || Tournament.getTournament().getState() == TournamentState.SELECTING_DUELS)) {
            Player player = event.getPlayer();
            Tournament.getTournament().getPlayers().remove(player.getUniqueId());
            Tournament.getTournament().getTeams().remove(Tournament.getTournament().getParticipant(player));
            Tournament.getTournament().setState(TournamentState.STARTING);
            Profile.get(player.getUniqueId()).setInTournament(false);
        }
    }

    @EventHandler
    public void onEndMatch(MatchEndEvent event) {
        Match match = event.getMatch();

        Tournament<?> tournament = Tournament.getTournament();
        if (tournament == null) return;
        for (GameParticipant<MatchGamePlayer> gameParticipant : match.getParticipants()) {
            if (gameParticipant.isAllDead()) {
                tournament.eliminatedTeam(gameParticipant);
            }
        }

        if (tournament.getMatches().contains(match)) {
            tournament.getMatches().remove(match);
            if (tournament.getMatches().isEmpty()) {
                if (tournament.getTeams().size() == 1) {
                    tournament.end(tournament.getTeams().get(0));
                } else if (tournament.getTeams().isEmpty()) {
                    tournament.end(null);
                } else {
                    if (tournament.getMatches().isEmpty()) {
                        tournament.nextRound();
                    }
                }
            }
        }
    }
}
