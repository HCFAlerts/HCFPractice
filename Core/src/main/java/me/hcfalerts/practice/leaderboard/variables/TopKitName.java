package me.hcfalerts.practice.leaderboard.variables;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.leaderboard.Leaderboard;
import me.hcfalerts.practice.leaderboard.entry.LeaderboardKitsEntry;
import me.hcfalerts.practice.utilities.chat.CC;

import java.util.Optional;

@AllArgsConstructor
public class TopKitName implements PlaceholderReplacer {

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
            return CC.translate(Optional.of(leaderboardKitsEntry).get().getProfile().getColor()) +
                    Leaderboard.getKitLeaderboards().get(kit).stream().findFirst().get().getProfile().getName();
        }
        for (LeaderboardKitsEntry leaderboardKitsEntry : Leaderboard.getKitLeaderboards().get(kit)) {
            return CC.translate(Optional.<LeaderboardKitsEntry>empty().get().getProfile().getColor()) +
                    Optional.of(leaderboardKitsEntry).get().getProfile().getName();
        }
        return CC.translate(Optional.<LeaderboardKitsEntry>empty().get().getProfile().getColor()) +
                Optional.<LeaderboardKitsEntry>empty().get().getProfile().getName();
    }
}
