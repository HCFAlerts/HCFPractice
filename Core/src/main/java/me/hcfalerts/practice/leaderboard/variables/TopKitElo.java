package me.hcfalerts.practice.leaderboard.variables;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.leaderboard.Leaderboard;
import me.hcfalerts.practice.leaderboard.entry.LeaderboardKitsEntry;

import java.util.Optional;

@AllArgsConstructor
public class TopKitElo implements PlaceholderReplacer {

    public String kit;

    @Override
    public String update() {
        if (Leaderboard.getKitLeaderboards().get(kit) == null) return " ";
        else {
            boolean found = true;
            for (LeaderboardKitsEntry ignored : Leaderboard.getKitLeaderboards().get(kit)) {
                found = false;
                break;
            }
            if (found) return " ";
        }

        for (LeaderboardKitsEntry leaderboardKitsEntry : Leaderboard.getKitLeaderboards().get(kit)) {
            return String.valueOf(Optional.of(leaderboardKitsEntry).get().getElo());
        }
        return String.valueOf(Optional.<LeaderboardKitsEntry>empty().get().getElo());
    }
}
