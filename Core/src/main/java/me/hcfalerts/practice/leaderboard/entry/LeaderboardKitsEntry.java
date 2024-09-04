package me.hcfalerts.practice.leaderboard.entry;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hcfalerts.practice.profile.Profile;

@Getter
@AllArgsConstructor
public class LeaderboardKitsEntry {

    private final Profile profile;
    private final int elo;

}
