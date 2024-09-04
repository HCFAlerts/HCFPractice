package me.hcfalerts.practice.match.impl;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.MatchSnapshot;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.follow.Follow;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.chat.ChatComponentBuilder;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BasicFreeForAllMatch extends Match {

	private final List<GameParticipant<MatchGamePlayer>> participants;
	private GameParticipant<MatchGamePlayer> winningParticipant;

	public BasicFreeForAllMatch(Queue queue, Kit kit, Arena arena, List<GameParticipant<MatchGamePlayer>> participants) {
		super(queue, kit, arena, false);

		this.participants = participants;
	}

	@Override
	public void setupPlayer(Player player) {
		super.setupPlayer(player);

		// Teleport the player to their spawn point
		Location spawn = arena.getSpawnA();

		if (spawn.getBlock().getType() == Material.AIR) player.teleport(spawn);
		else player.teleport(spawn.add(0, 2, 0));

		if (Follow.getByFollowed(player.getUniqueId()) != null) {
			Follow follow = Follow.getByFollowed(player.getUniqueId());

			addSpectator(Bukkit.getPlayer(follow.getFollower()), player, true);
		}
	}

	@Override
	public boolean canEndMatch() {
		return getRemainingTeams() <= 1;
	}

	@Override
	public boolean canStartRound() {
		return false;
	}

	@Override
	public boolean canEndRound() {
		return getRemainingTeams() <= 1;
	}

	@Override
	public void onRoundEnd() {
		for (GameParticipant<MatchGamePlayer> gameParticipant : participants) {
			if (!gameParticipant.isAllDead()) {
				winningParticipant = gameParticipant;
				break;
			}
		}

		// Send Death Effects
		for (MatchGamePlayer player : winningParticipant.getPlayers()) {
			if (!player.isDisconnected() && !player.isDead()) {
				Profile.get(player.getUuid()).getDeathEffect().apply();
			}
		}

		if (!kit.getGameRules().isSumo() && !kit.getGameRules().isBridge() && !kit.getGameRules().isParkour()) {
			// Make all snapshots available
			for (MatchSnapshot snapshot : snapshots) {
				snapshot.setCreatedAt(System.currentTimeMillis());
				MatchSnapshot.getSnapshots().put(snapshot.getUuid(), snapshot);
			}
		}

		super.onRoundEnd();
	}

	@Override
	public boolean isOnSameTeam(Player first, Player second) {
		return first.equals(second);
	}

	@Override
	public List<GameParticipant<MatchGamePlayer>> getParticipants() {
		return new ArrayList<>(participants);
	}

	@Override
	public ChatColor getRelationColor(Player viewer, Player target) {
		if (viewer.equals(target)) return ChatColor.GREEN;
		else {
			for (GameParticipant<MatchGamePlayer> participant : participants)
				if (participant.containsPlayer(target.getUniqueId()))
					return ChatColor.RED;

			return ChatColor.YELLOW;
		}
	}

	@Override
	public List<String> getScoreboardLines(Player player) {
		List<String> lines = new ArrayList<>();
		BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

		if (getParticipant(player) != null && !getGamePlayer(player).isDead()) {
			for (String s : config.getStringList("FIGHTS.PARTY-FFA.IS-ALIVE")) {
				lines.add(s.replace("{duration}", getDuration())
						.replace("{opponents-size}", String.valueOf(getRemainingTeams() - 1)));
			}
		} else {
			for (String s : config.getStringList("FIGHTS.PARTY-FFA.IS-DEAD")) {
				lines.add(s.replace("{kit}", getKit().getName())
						.replace("{duration}", getDuration())
						.replace("{teams}", String.valueOf(getRemainingTeams())));
			}
		}

		return lines;
	}

	@Override
	public List<String> getSpectatorScoreboardLines() {
		List<String> lines = new ArrayList<>();
		BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

		for (String s : config.getStringList("SPECTATE.PARTY-FFA.LINES")) {
			lines.add(s.replace("{bars}", CC.SB_BAR)
					.replace("{kit}", getKit().getName())
					.replace("{duration}", getDuration())
					.replace("{arena-author}", getArena().getAuthor())
					.replace("{players-alive}", String.valueOf(getRemainingTeams()))
					.replace("{players-total}", String.valueOf(getParticipants().size())));
		}

		return lines;
	}

	@Override
	public void addSpectator(Player spectator, Player target, boolean follow) {
		super.addSpectator(spectator, target, follow);
	}

	@Override
	public List<BaseComponent[]> generateEndComponents(Player player) {
		List<BaseComponent[]> componentsList = new ArrayList<>();
		Profile profile = Profile.get(player.getUniqueId());

		for (String line : Locale.MATCH_END_DETAILS.getStringList(profile.getLocale())) {
			if (line.equalsIgnoreCase("%INVENTORIES%")) {
				List<GameParticipant<MatchGamePlayer>> participants = new ArrayList<>(this.participants);
				participants.remove(winningParticipant);

				BaseComponent[] winners = generateInventoriesComponents(
					new MessageFormat(Locale.MATCH_END_WINNER_INVENTORY.format(profile.getLocale()))
						.add("{context}", "").toString(), winningParticipant);

				BaseComponent[] losers = generateInventoriesComponents(
						new MessageFormat(Locale.MATCH_END_LOSER_INVENTORY.format(profile.getLocale()))
							.add("{context}", participants.size() > 1 ? "s" : "").toString(), participants);

				componentsList.add(winners);
				componentsList.add(losers);

				continue;
			}

			if (line.equalsIgnoreCase("%ELO_CHANGES%")) continue;

			line = line.replace("{spectators}", String.valueOf(getSpectators().size()));

			componentsList.add(new ChatComponentBuilder("").parse(line).create());
		}

		return componentsList;
	}

	private int getRemainingTeams() {
		int remaining = 0;

		for (GameParticipant<MatchGamePlayer> gameParticipant : participants) {
			if (!gameParticipant.isAllDead()) {
				remaining++;
			}
		}

		return remaining;
	}

}
