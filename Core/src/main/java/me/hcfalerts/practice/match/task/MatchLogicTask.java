package me.hcfalerts.practice.match.task;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.MatchState;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;
import org.github.paperspigot.Title;

public class MatchLogicTask extends BukkitRunnable {

	private final Match match;
	@Getter @Setter private int nextAction;

	public MatchLogicTask(Match match) {
		this.match = match;

		if (match.getKit().getGameRules().isSumo()) {
			nextAction = 4;
		} else {
			nextAction = 6;
		}
	}

	@Override
	public void run() {
		nextAction--;
		if (match.getState() == MatchState.STARTING_ROUND) {
			if (nextAction == 0) {
				for (GameParticipant<MatchGamePlayer> participant : match.getParticipants()) {
					for (MatchGamePlayer player : participant.getPlayers()) {
						if (player.getPlayer() != null)
							player.getPlayer().sendTitle(new Title(new MessageFormat(Locale.MATCH_STARTED_TITLE
									.format(Profile.get(player.getUuid()).getLocale())).getMessage(), null, 20, 15, 20));
					}
				}
				TaskUtil.run(match::onRoundStart);
				match.setState(MatchState.PLAYING_ROUND);
				match.sendMessage(Locale.MATCH_STARTED, new MessageFormat());
				match.sendSound(Sound.ORB_PICKUP, 1.0F, 1.0F);
			} else {
				for (GameParticipant<MatchGamePlayer> participant : match.getParticipants()) {
					for (MatchGamePlayer player : participant.getPlayers()) {
						if (player.getPlayer() != null)
							TaskUtil.run(() -> player.getPlayer().sendTitle(
									new Title(ChatColor.RED + String.valueOf(nextAction), null, 20, 20, 20)));
					}
				}
				match.sendMessage(Locale.MATCH_START_TIMER, new MessageFormat()
					.add("{time}", String.valueOf(nextAction))
					.add("{context}", nextAction == 1 ? "" : "s")
				);
				match.sendSound(Sound.ORB_PICKUP, 1.0F, 15F);
			}
		}
		else if (match.getState() == MatchState.ENDING_ROUND) {
			if (nextAction == 0) {
				if (match.canStartRound()) {
					for (GameParticipant<MatchGamePlayer> participant : match.getParticipants()) {
						for (MatchGamePlayer player : participant.getPlayers()) {
							if (player.getPlayer() != null) {
								if (Profile.get(player.getUuid()).getDeathEffect() != null) Profile.get(player.getUuid()).getDeathEffect().stop();
								match.setupPlayer(player.getPlayer());
							}
						}
					}
					match.setState(MatchState.STARTING_ROUND);
					match.getLogicTask().setNextAction(4);
				}
			}
		}
		else if (match.getState() == MatchState.ENDING_MATCH) {
			if (nextAction == 0) TaskUtil.run(match::end);
		}
	}

}
