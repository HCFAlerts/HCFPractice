package me.hcfalerts.practice.event.impl.tnttag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.GamePlayer;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@AllArgsConstructor
public class MatchTask extends BukkitRunnable {

    public final TNTTagGameLogic tntTagGameLogic;
    @Getter public int seconds;

    @Override
    public void run() {
        if (Arrays.asList(30, 15, 10, 5, 4, 3, 2, 1).contains(seconds)) {
            for (GameParticipant<GamePlayer> participant : tntTagGameLogic.getParticipants()) {
                new MessageFormat(Locale.EVENT_MATCH_REMAINING.format(Profile.get(participant.getLeader().getUuid()).getLocale()))
                        .add("{seconds}", String.valueOf(seconds))
                        .add("{context}", seconds == 1 ? "" : "s")
                        .send(participant.getLeader().getPlayer());
            }
        }
        if (seconds < 1) {
            tntTagGameLogic.endRound();
            cancel();
        }

        seconds--;
    }
}
