package me.hcfalerts.practice.tournament;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.tournament.impl.TournamentSolo;
import me.hcfalerts.practice.tournament.impl.TournamentTeams;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.countdown.Countdown;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static me.hcfalerts.practice.tournament.TournamentState.ENDED;
import static me.hcfalerts.practice.tournament.TournamentState.IN_FIGHT;

@Getter @Setter
public abstract class Tournament<T>{

    @Getter @Setter private static Tournament<?> tournament;

    private boolean started = false;
    private TournamentState state = TournamentState.STARTING;
    private final List<UUID> players = Lists.newArrayList();
    private int size, limit;
    private List<GameParticipant<MatchGamePlayer>> teams = Lists.newArrayList();
    private Kit kit;
    private final List<Match> matches = Lists.newArrayList();
    private boolean clans;
    private int round = 0;
    private GameParticipant<MatchGamePlayer> winner;
    public Countdown countdown;

    public abstract void join(T type);

    public abstract void start();

    public abstract void nextRound();

    public abstract void eliminatedTeam(GameParticipant<MatchGamePlayer> teamEliminated);

    public abstract void end(GameParticipant<MatchGamePlayer> winner);

    public void broadcast(String msg){
        for (GameParticipant<MatchGamePlayer> team : teams) {
            team.getPlayers().forEach(matchGamePlayer -> matchGamePlayer.getPlayer().sendMessage(CC.translate(msg)));
        }
    }

    public void broadcast(Locale locale, HashMap<String, String> variables){
        for (GameParticipant<MatchGamePlayer> team : teams) {
            for (MatchGamePlayer player : team.getPlayers()) {
                MessageFormat messageFormat = new MessageFormat(locale.format(Profile.get(player.getUuid()).getLocale()));
                messageFormat.setVariables(variables);
                messageFormat.send(player.getPlayer());
            }
        }
    }

    public List<Player> getOnlinePlayers(){
        List<Player> list = new ArrayList<>();
        for (UUID player : players) {
            Player player1 = Bukkit.getPlayer(player);
            if (player1 != null) {
                list.add(player1);
            }
        }
        return list;
    }

    public GameParticipant<MatchGamePlayer> getParticipant(Player player) {
        for (GameParticipant<MatchGamePlayer> team : teams) {
            for (MatchGamePlayer teamPlayer : team.getPlayers()) {
                if (teamPlayer.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                    return team;
                }
            }
        }
        return null;
    }

    public List<String> getTournamentScoreboard() {
        List<String> lines = Lists.newArrayList();
        BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();
        String mode;
        if (tournament instanceof TournamentSolo) mode = "Solo";
        else if (tournament instanceof TournamentTeams) mode = "Team";
        else mode = "Clan";

        for (String s : config.getStringList("BOARD.TOURNAMENT.LINES")) {
            if (s.contains("{in-fight}")) {
                if (this.getState() == IN_FIGHT) {
                    for (String line : config.getStringList("BOARD.TOURNAMENT.IN-FIGHT")) {
                        lines.add(line.replace("{round}", String.valueOf(getRound())));
                    }
                }
                continue;
            }
            if (s.contains("{end}")) {
                if (this.getState() == ENDED && getWinner() != null) {
                    MatchGamePlayer leader = getWinner().getLeader();
                    for (String line : config.getStringList("BOARD.TOURNAMENT.END")) {
                        lines.add(line.replace("{color}", Profile.get(leader.getPlayer().getUniqueId()).getColor())
                                .replace("{player}", leader.getPlayer().getName()));
                    }
                }
                continue;
            }
            lines.add(s.replace("{kit}", getKit().getName())
                    .replace("{size}", String.valueOf(getTeams().size()))
                    .replace("{limit}", String.valueOf(getLimit()))
                    .replace("{state}", getState().getName())
                    .replace("{mode}", mode));
        }
        return lines;
    }
}