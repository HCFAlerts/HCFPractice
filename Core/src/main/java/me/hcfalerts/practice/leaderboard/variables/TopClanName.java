package me.hcfalerts.practice.leaderboard.variables;

import com.gmail.filoghost.holographicdisplays.api.placeholder.PlaceholderReplacer;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.leaderboard.Leaderboard;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class TopClanName implements PlaceholderReplacer {

    public int pos;

    @Override
    public String update() {
        try {
            if (Leaderboard.getClanLeaderboards().keySet().isEmpty()) return " ";
            List<String> names = new ArrayList<>(Leaderboard.getClanLeaderboards().keySet());
            if (names.get(pos) == null) return " ";
            if (Clan.getByName(names.get(pos)) == null) return " ";
            return Clan.getByName(names.get(pos)).getColoredName();
        } catch (IndexOutOfBoundsException e) {
            return " ";
        }
    }
}
