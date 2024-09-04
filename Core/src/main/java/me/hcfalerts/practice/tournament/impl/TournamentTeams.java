package me.hcfalerts.practice.tournament.impl;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.TeamGameParticipant;
import me.hcfalerts.practice.tournament.Tournament;
import me.hcfalerts.practice.tournament.events.TournamentEndEvent;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.countdown.Countdown;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import static me.hcfalerts.practice.tournament.TournamentState.*;

@Getter @Setter
public class TournamentTeams extends Tournament<Party> {

    public void start(){
        setState(IN_FIGHT);
        nextRound();
    }

    public void join(Party party){
        Player partyLeader = party.getLeader();

        MatchGamePlayer leader = new MatchGamePlayer(partyLeader.getUniqueId(), partyLeader.getName());

        TeamGameParticipant<MatchGamePlayer> teamGameParticipant = new TeamGameParticipant<>(leader);

        for (Player player : party.getListOfPlayers()) {
            getPlayers().add(player.getUniqueId());
            Profile.get(player.getPlayer().getUniqueId()).setInTournament(true);
            if (!player.getPlayer().equals(partyLeader)) {
                MatchGamePlayer gamePlayer = new MatchGamePlayer(player.getUniqueId(), player.getName());
                teamGameParticipant.getPlayers().add(gamePlayer);
            }
        }
        getTeams().add(teamGameParticipant);

        HashMap<String, String> variables = Maps.newHashMap();
        variables.put("{leader}", partyLeader.getDisplayName());
        broadcast(Locale.TOURNAMENT_PARTY_JOIN, variables);

        if(getTeams().size() == getLimit()){
            countdown = Countdown.of(15, TimeUnit.SECONDS)
                .players(getOnlinePlayers())
                .broadcastAt(15, TimeUnit.SECONDS)
                .broadcastAt(10, TimeUnit.SECONDS)
                .broadcastAt(5, TimeUnit.SECONDS)
                .broadcastAt(4, TimeUnit.SECONDS)
                .broadcastAt(3, TimeUnit.SECONDS)
                .broadcastAt(2, TimeUnit.SECONDS)
                .broadcastAt(1, TimeUnit.SECONDS)
                .withMessage(Locale.TOURNAMENT_START)
                .onFinish(this::start)
                .start();
        }
    }

    public void nextRound(){
        setState(SELECTING_DUELS);
        setRound(getRound() + 1);
        //Shuffle list to randomize
        Collections.shuffle(getTeams());
        //New team LinkedList to remove usedTeams
        LinkedList<GameParticipant<MatchGamePlayer>> teamsShuffle = new LinkedList<>(getTeams());
        //Count down
        Locale round = Locale.TOURNAMENT_NEXT_ROUND;
        if (getRound() == 1) round = Locale.TOURNAMENT_STARTING_MATCH;
        countdown = Countdown.of(10, TimeUnit.SECONDS)
            .players(getOnlinePlayers())
            .broadcastAt(10, TimeUnit.SECONDS)
            .broadcastAt(5, TimeUnit.SECONDS)
            .broadcastAt(4, TimeUnit.SECONDS)
            .broadcastAt(3, TimeUnit.SECONDS)
            .broadcastAt(2, TimeUnit.SECONDS)
            .broadcastAt(1, TimeUnit.SECONDS)
            .withMessage(round)
            .onFinish(() -> {
                setState(IN_FIGHT);
                //Logic to start match
                TaskUtil.runTimer(new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(teamsShuffle.isEmpty()) {
                            cancel();
                            return;
                        }
                        GameParticipant<MatchGamePlayer> teamA = teamsShuffle.poll();
                        if(teamsShuffle.isEmpty()) {
                            end(teamA);
                            for (MatchGamePlayer matchGamePlayer : teamA.getPlayers()) {
                                new MessageFormat(Locale.TOURNAMENT_NO_PLAYER_FOUND
                                        .format(Profile.get(matchGamePlayer.getUuid()).getLocale()))
                                        .send(matchGamePlayer.getPlayer());
                            }
                            return;
                        }
                        GameParticipant<MatchGamePlayer> teamB = teamsShuffle.poll();

                        Arena arena = Arena.getRandomArena(getKit());

                        if (arena == null) {
                            for (MatchGamePlayer gamePlayer : teamA.getPlayers()) {
                                new MessageFormat(Locale.TOURNAMENT_TRY_START_WITHOUT_ARENAS
                                        .format(Profile.get(gamePlayer.getUuid()).getLocale()))
                                        .send(gamePlayer.getPlayer());
                            }
                            for (MatchGamePlayer gamePlayer : teamB.getPlayers()) {
                                new MessageFormat(Locale.TOURNAMENT_TRY_START_WITHOUT_ARENAS
                                        .format(Profile.get(gamePlayer.getUuid()).getLocale()))
                                        .send(gamePlayer.getPlayer());
                            }
                            return;
                        }
                        arena.setBusy(true);
                        Match match = new BasicTeamMatch(null, getKit(), arena, false, teamA, teamB);
                        match.start();
                        getMatches().add(match);
                    }
                }, 1, 1);
            }).start();
    }

    public void eliminatedTeam(GameParticipant<MatchGamePlayer> teamEliminated) {
        getTeams().remove(teamEliminated);
        for (GameParticipant<MatchGamePlayer> team : getTeams()) {
            for (MatchGamePlayer matchGamePlayer : team.getPlayers()) {
                Profile.get(matchGamePlayer.getPlayer().getUniqueId()).setInTournament(false);
                getPlayers().remove(matchGamePlayer.getUuid());
                Player player = matchGamePlayer.getPlayer();
                if (player != null) new MessageFormat(Locale.TOURNAMENT_ELIMINATE_TEAM
                        .format(Profile.get(player.getUniqueId()).getLocale()))
                        .add("{team}", teamEliminated.getConjoinedNames())
                        .send(player);
            }
        }
    }

    public void end(GameParticipant<MatchGamePlayer> winner) {
        for (GameParticipant<MatchGamePlayer> team : getTeams()) {
            for (MatchGamePlayer matchGamePlayer : team.getPlayers()) {
                Profile.get(matchGamePlayer.getPlayer().getUniqueId()).setInTournament(false);
            }
        }
        getTeams().clear();
        getPlayers().clear();
        setStarted(false);
        setTournament(null);
        setWinner(winner);
        setState(ENDED);
        if(winner != null){
            new TournamentEndEvent(winner, false, false).call();
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                new MessageFormat(Locale.TOURNAMENT_WINNER_MESSAGE
                        .format(Profile.get(onlinePlayer.getUniqueId()).getLocale()))
                        .add("{winner}", winner.getConjoinedNames())
                        .send(onlinePlayer);
            }
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                new MessageFormat(Locale.TOURNAMENT_STOP
                        .format(Profile.get(onlinePlayer.getUniqueId()).getLocale()))
                        .send(onlinePlayer);
            }
        }
    }
}