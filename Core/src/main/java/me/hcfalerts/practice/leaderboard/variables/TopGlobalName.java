package me.hcfalerts.practice.leaderboard.variables;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.leaderboard.Leaderboard;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;

import java.util.List;

@AllArgsConstructor
public class TopGlobalName implements PlaceholderReplacer {

    public int pos;

    @Override
    public String update() {
        try {
            if (Leaderboard.getLeaderboards().isEmpty()) return " ";
            List<Profile> test = Leaderboard.getLeaderboards();
            if (test.get(pos) == null) return " ";
            Profile profile = test.get(pos);
            return CC.translate(profile.getColor()) + profile.getName();
        } catch (IndexOutOfBoundsException e) {
            return " ";
        }
    }
}