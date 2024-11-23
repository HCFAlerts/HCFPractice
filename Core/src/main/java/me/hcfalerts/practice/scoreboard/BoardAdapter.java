package me.hcfalerts.practice.scoreboard;

import com.google.common.collect.Lists;
import me.clip.placeholderapi.PlaceholderAPI;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.modmode.ModMode;
import me.hcfalerts.practice.queue.QueueProfile;
import me.hcfalerts.practice.scoreboard.impl.Assemble;
import me.hcfalerts.practice.scoreboard.impl.AssembleAdapter;
import me.hcfalerts.practice.tournament.Tournament;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.elo.EloUtil;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.Animation;
import me.hcfalerts.practice.utilities.string.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class BoardAdapter implements AssembleAdapter {

	private final BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

	@Override
	public String getTitle(Player player) {
		return CC.translate(Animation.getScoreboardTitle());
	}

	@Override
	public List<String> getLines(Player player) {
		Profile profile = Profile.get(player.getUniqueId());
		if (!profile.getOptions().showScoreboard()) return Lists.newArrayList();
		List<String> lines = Lists.newArrayList();

		if (profile.getState() == ProfileState.LOBBY) {
			for (String s : config.getStringList("BOARD.LOBBY")) {
				if (s.contains("{tournament}")) {
					if (Tournament.getTournament() != null) {
						lines.addAll(Tournament.getTournament().getTournamentScoreboard());
					}
					continue;
				}
				if (s.contains("{party}")) {
					if (profile.getParty() != null && Tournament.getTournament() == null) {
						Party party = profile.getParty();
						for (String s1 : config.getStringList("BOARD.PARTY")) {
							lines.add(s1
									.replace("{hoster}", party.getLeader().getName())
									.replace("{players}", String.valueOf(party.getPlayers().size())));
						}
					}
					continue;
				}
				if (s.contains("{clan}")) {
					if (profile.getClan() != null && profile.getParty() == null && Tournament.getTournament() == null) {
						Clan clan = profile.getClan();
						for (String s1 : config.getStringList("BOARD.CLAN")) {
							lines.add(s1
									.replace("{clan}", clan.getName())
									.replace("{color}", clan.getColor().toString())
									.replace("{size}", String.valueOf(clan.getMembers().size())));
						}
					}
					continue;
				}
				if (s.contains("{following}")) {
					if (profile.getFollow() != null) {
						lines.addAll(profile.getFollow().getScoreboardLines());
					}
					continue;
				}
				lines.add(s
						.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))
						.replace("{in-fights}", String.valueOf(HCFPractice.get().getInFightsTotal()))
						.replace("{in-fights-ranked}", String.valueOf(HCFPractice.get().getInFightsRanked()))
						.replace("{in-fights-unranked}", String.valueOf(HCFPractice.get().getInFightsUnRanked()))
						.replace("{in-queue}", String.valueOf(HCFPractice.get().getInQueues()))
						.replace("{elo}", String.valueOf(EloUtil.getGlobalElo(profile)))
						.replace("{category}", String.valueOf(profile.getCategory().getDisplayName())));
			}
		}
		else if (profile.getState() == ProfileState.QUEUEING) {
			QueueProfile queueProfile = profile.getQueueProfile();

			for (String s : config.getStringList("BOARD.QUEUE.LINES")) {
				if (s.contains("{ranked}")) {
					if (queueProfile.getQueue().isRanked()) {
						for (String s1 : config.getStringList("BOARD.QUEUE.RANKED")) {
							lines.add(s1
									.replace("{min-range}", String.valueOf(queueProfile.getMinRange()))
									.replace("{max-range}", String.valueOf(queueProfile.getMaxRange()))
									.replace("{elapsed}", TimeUtil.millisToTimer(queueProfile.getPassed()))
									.replace("{queue}", queueProfile.getQueue().getQueueName()));
						}
					}
					continue;
				}
				lines.add(s
						.replace("{online}", String.valueOf(Bukkit.getOnlinePlayers().size()))
						.replace("{in-fights}", String.valueOf(HCFPractice.get().getInFightsTotal()))
						.replace("{in-fights-ranked}", String.valueOf(HCFPractice.get().getInFightsRanked()))
						.replace("{in-fights-unranked}", String.valueOf(HCFPractice.get().getInFightsUnRanked()))
						.replace("{in-queue}", String.valueOf(HCFPractice.get().getInQueues()))
						.replace("{elo}", String.valueOf(EloUtil.getGlobalElo(profile)))
						.replace("{category}", String.valueOf(profile.getCategory().getDisplayName()))
						.replace("{elapsed}", TimeUtil.millisToTimer(queueProfile.getPassed()))
						.replace("{queue}", queueProfile.getQueue().getQueueName()));
			}
		}
		else if (profile.getState() == ProfileState.STAFF_MODE) {
			lines.addAll(ModMode.getScoreboardLines(player));
		}
		else if (profile.getState() == ProfileState.EVENT) {
			if (EventGame.getActiveGame() != null) {
				lines.addAll(EventGame.getActiveGame().getGameLogic().getScoreboardEntries());
			}
		}
		else if (profile.getState() == ProfileState.FIGHTING) {
			lines.addAll(profile.getMatch().getScoreboardLines(player));
		}
		else if (profile.getState() == ProfileState.SPECTATING) {
			lines.addAll(profile.getMatch().getSpectatorScoreboardLines());
		}

		if (lines.contains("{footer}")) {
			lines.remove("{footer}");
			if (config.getBoolean("FOOTER_ENABLED")) {
				lines.add("");
				lines.add(Animation.getScoreboardFooter());
			}
		}
		if (config.getBoolean("BARS_ENABLED")) {
			String bars = config.getString("BARS_FORMAT");
			lines.add(0, bars);
			lines.add(bars);
		}

		return HCFPractice.get().isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders(player, lines) : lines;
	}

	public static void hook() {
		HCFPractice.get().assemble = new Assemble(HCFPractice.get(), new BoardAdapter());
		HCFPractice.get().getAssemble().setTicks(2);
	}
}
