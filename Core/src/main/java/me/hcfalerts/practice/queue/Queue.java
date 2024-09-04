package me.hcfalerts.practice.queue;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.queue.thread.QueueThread;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Getter
public class Queue {

	@Getter private static final List<Queue> queues = new ArrayList<>();

	@Getter public static final boolean pingRangeBoolean = HCFPractice.get().getMainConfig().getBoolean("QUEUE.RANKED_PING_RANGE_BOOLEAN");
	@Getter public static int pingRange = HCFPractice.get().getMainConfig().getInteger("QUEUE.RANKED_PING_RANGE");

	private final UUID uuid = UUID.randomUUID();
	private final Kit kit;
	@Setter private boolean ranked;
	private final LinkedList<QueueProfile> players = new LinkedList<>();

	public Queue(Kit kit, boolean ranked) {
		this.kit = kit;
		this.ranked = ranked;

		queues.add(this);
	}

	public String getQueueName() {
		return (ranked ? "Ranked" : "Unranked") + " " + kit.getName();
	}

	public void addPlayer(Player player, int elo) {
		QueueProfile queueProfile = new QueueProfile(this, player.getUniqueId());
		queueProfile.setElo(elo);

		Profile profile = Profile.get(player.getUniqueId());
		profile.setQueueProfile(queueProfile);
		profile.setState(ProfileState.QUEUEING);

		players.add(queueProfile);

		Hotbar.giveHotbarItems(player);

		if (ranked) {
			new MessageFormat(Locale.QUEUE_JOIN_RANKED.format(profile.getLocale()))
				.add("{kit_name}", kit.getName())
				.add("{elo}", String.valueOf(elo))
				.send(player);
		} else {
			new MessageFormat(Locale.QUEUE_JOIN_UNRANKED.format(profile.getLocale()))
				.add("{kit_name}", kit.getName())
				.send(player);
		}
	}

	public void removePlayer(QueueProfile queueProfile) {
		players.remove(queueProfile);

		Profile profile = Profile.get(queueProfile.getPlayerUuid());
		profile.setQueueProfile(null);
		profile.setState(ProfileState.LOBBY);

		Player player = Bukkit.getPlayer(queueProfile.getPlayerUuid());

		if (player != null) {
			Hotbar.giveHotbarItems(player);

			if (ranked) {
				new MessageFormat(Locale.QUEUE_LEAVE_RANKED.format(profile.getLocale()))
					.add("{kit_name}", kit.getName())
					.send(player);
			} else {
				new MessageFormat(Locale.QUEUE_LEAVE_UNRANKED.format(profile.getLocale()))
					.add("{kit_name}", kit.getName())
					.send(player);
			}
		}

	}

	public static Queue getByUuid(UUID uuid) {
		for (Queue queue : queues) {
			if (queue.getUuid().equals(uuid)) {
				return queue;
			}
		}

		return null;
	}

	public static QueueProfile getQueueProfileByUuid(UUID uuid) {
		for (Queue queue : queues) {
            for (QueueProfile queueProfile : queue.getPlayers()) {
                if (queueProfile.getPlayerUuid().equals(uuid)) {
                    return queueProfile;
                }
            }
        }

		return null;
	}

	public static void init() {
		new QueueThread().start();
		TaskUtil.runTimerAsync(() -> {
			int queue = 0, match = 0, ranked = 0, unranked = 0;

			for (Queue queue1 : queues) {
				for (QueueProfile ignored : queue1.getPlayers()) {
					queue++;
				}
			}
			HCFPractice.get().inQueues = queue;

			for (Match match1 : Match.getMatches()) {
				for (GameParticipant<MatchGamePlayer> participant : match1.getParticipants()) {
					for (MatchGamePlayer player : participant.getPlayers()) {
						if (match1.getKit().getGameRules().isRanked()) ranked++;
						else unranked++;
						match++;
					}
				}
			}
			HCFPractice.get().inFightsTotal = match;
			HCFPractice.get().inFightsUnRanked = unranked;
			HCFPractice.get().inFightsRanked = ranked;
		}, 2L, 2L);
	}
}
