package me.hcfalerts.practice.tournament.impl;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.var;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.TeamGameParticipant;
import me.hcfalerts.practice.tournament.Tournament;
import me.hcfalerts.practice.tournament.events.TournamentEndEvent;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.countdown.Countdown;
import me.hcfalerts.practice.utilities.countdown.CountdownBuilder;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static me.hcfalerts.practice.tournament.TournamentState.*;

@Getter
public class TournamentClans extends Tournament<Clan> {

    private final Map<Clan, TeamGameParticipant<MatchGamePlayer>> clans = Maps.newHashMap();

    public void start(){
        nextRound();
    }

    public void join(Clan clan){
        Player clanLeader = Bukkit.getPlayer(clan.getLeader());

        MatchGamePlayer leader = new MatchGamePlayer(clanLeader.getUniqueId(), clanLeader.getName());

        TeamGameParticipant<MatchGamePlayer> teamGameParticipant = new TeamGameParticipant<>(leader);

        for (Player player : clan.getOnPlayers()) {
            getPlayers().add(player.getUniqueId());
            Profile.get(player.getPlayer().getUniqueId()).setInTournament(true);
            if (!player.getPlayer().equals(clanLeader)) {
                MatchGamePlayer gamePlayer = new MatchGamePlayer(player.getUniqueId(), player.getName());
                teamGameParticipant.getPlayers().add(gamePlayer);
            }
        }
        getTeams().add(teamGameParticipant);

        clans.put(clan, teamGameParticipant);

        HashMap<String, String> variables = Maps.newHashMap();
        variables.put("{clan}", clan.getName());
        variables.put("{color}", clan.getColor().toString());
        broadcast(Locale.TOURNAMENT_CLAN_JOIN, variables);

        if (getTeams().size() == getLimit()) {
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
                .onFinish(this::start).start();
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
        CountdownBuilder countdownBuilder = Countdown.of(10, TimeUnit.SECONDS);
        countdownBuilder.players(getOnlinePlayers());
        countdownBuilder.broadcastAt(10, TimeUnit.SECONDS);
        countdownBuilder.broadcastAt(5, TimeUnit.SECONDS);
        countdownBuilder.broadcastAt(4, TimeUnit.SECONDS);
        countdownBuilder.broadcastAt(3, TimeUnit.SECONDS);
        countdownBuilder.broadcastAt(2, TimeUnit.SECONDS);
        countdownBuilder.broadcastAt(1, TimeUnit.SECONDS);
        countdownBuilder.withMessage(round);
        countdownBuilder.onFinish(() -> {
            setState(IN_FIGHT);
            //Logic to start match
            TaskUtil.runTimer(new BukkitRunnable() {
                @Override
                public void run() {
                    if (teamsShuffle.isEmpty()) {
                        cancel();
                        return;
                    }
                    GameParticipant<MatchGamePlayer> teamA = teamsShuffle.poll();
                    if (teamsShuffle.isEmpty()) {
                        end(teamA);
                        teamA.getPlayers().forEach(matchGamePlayer -> new MessageFormat(Locale.TOURNAMENT_NO_PLAYER_FOUND
                                .format(Profile.get(matchGamePlayer.getUuid()).getLocale()))
                                .send(matchGamePlayer.getPlayer()));
                        return;
                    }
                    GameParticipant<MatchGamePlayer> teamB = teamsShuffle.poll();

                    Arena arena = Arena.getRandomArena(getKit());

                    if (arena == null) {
                        teamA.getPlayers().forEach(matchGamePlayer -> new MessageFormat(Locale.TOURNAMENT_TRY_START_WITHOUT_ARENAS
                                .format(Profile.get(matchGamePlayer.getUuid()).getLocale()))
                                .send(matchGamePlayer.getPlayer()));
                        teamB.getPlayers().forEach(matchGamePlayer -> new MessageFormat(Locale.TOURNAMENT_TRY_START_WITHOUT_ARENAS
                                .format(Profile.get(matchGamePlayer.getUuid()).getLocale()))
                                .send(matchGamePlayer.getPlayer()));
                        return;
                    }
                    arena.setBusy(true);
                    Match match = new BasicTeamMatch(null, getKit(), arena, false, teamA, teamB);
                    match.start();
                    getMatches().add(match);
                }
            }, 1, 1);
        });
        countdown = countdownBuilder.start();
    }

    public void eliminatedTeam(GameParticipant<MatchGamePlayer> teamEliminated) {
        getTeams().remove(teamEliminated);
        var leader = teamEliminated.getLeader();
        var profile = Profile.get(leader.getPlayer().getUniqueId());
        var clan = profile.getClan();
        for (GameParticipant<MatchGamePlayer> team : getTeams()) {
            for (MatchGamePlayer matchGamePlayer : team.getPlayers()) {
                Profile.get(matchGamePlayer.getUuid()).setInTournament(false);
                Player player = matchGamePlayer.getPlayer();
                getPlayers().remove(matchGamePlayer.getUuid());
                if (player != null) new MessageFormat(Locale.TOURNAMENT_ELIMINATE_CLAN
                        .format(Profile.get(player.getUniqueId()).getLocale()))
                        .add("{clan}", clan.getName())
                        .add("{color}", clan.getColor().toString())
                        .send(player);
            }
        }
    }

    public void end(GameParticipant<MatchGamePlayer> winner){
        for (GameParticipant<MatchGamePlayer> team : getTeams()) {
            team.getPlayers().forEach(matchGamePlayer ->
                    Profile.get(matchGamePlayer.getPlayer().getUniqueId()).setInTournament(false));
        }
        getTeams().clear();
        getPlayers().clear();
        setStarted(false);
        setTournament(null);
        setWinner(winner);
        setState(ENDED);
        if (winner != null) {
            new TournamentEndEvent(winner, false, true).call();
            MatchGamePlayer leader = winner.getLeader();
            Profile profile = Profile.get(leader.getPlayer().getUniqueId());
            Clan clan = profile.getClan();

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                new MessageFormat(Locale.TOURNAMENT_WINNER_MESSAGE
                        .format(Profile.get(onlinePlayer.getUniqueId()).getLocale()))
                        .add("{winner}", clan.getColoredName())
                        .send(onlinePlayer);
            }

            clan.setTournamentWins(clan.getTournamentWins() + 1);
        } else {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                new MessageFormat(Locale.TOURNAMENT_STOP
                        .format(Profile.get(onlinePlayer.getUniqueId()).getLocale()))
                        .send(onlinePlayer);
            }
        }
        setTournament(null);
        if (countdown != null) countdown.stop();
    }
}