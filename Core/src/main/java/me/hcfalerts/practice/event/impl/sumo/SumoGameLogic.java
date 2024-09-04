package me.hcfalerts.practice.event.impl.sumo;

import com.google.common.collect.Lists;
import lombok.Getter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameLogic;
import me.hcfalerts.practice.event.game.EventGameLogicTask;
import me.hcfalerts.practice.event.game.EventGameState;
import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.event.game.map.vote.EventGameMapVoteData;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.knockback.Knockback;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.GamePlayer;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.BlockUtil;
import me.hcfalerts.practice.utilities.Cooldown;
import me.hcfalerts.practice.utilities.PlayerUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class SumoGameLogic implements EventGameLogic {

	private final EventGame game;
	@Getter private GameParticipant<GamePlayer> participantA;
	@Getter private GameParticipant<GamePlayer> participantB;
	@Getter private int roundNumber;
	@Getter private final EventGameLogicTask logicTask;
	private GameParticipant<GamePlayer> winningParticipant;

	public SumoGameLogic(EventGame game) {
		this.game = game;
		this.logicTask = new EventGameLogicTask(game);
		this.logicTask.runTaskTimer(HCFPractice.get(), 0, 20L);
	}

	@Override
	public EventGameLogicTask getGameLogicTask() {
		return logicTask;
	}

	@Override
	public void startEvent() {
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.get(onlinePlayer.getUniqueId());
			new MessageFormat(me.hcfalerts.practice.Locale.EVENT_START.format(profile.getLocale()))
					.add("{event_name}", game.getEvent().getName())
					.add("{event_displayname}", game.getEvent().getDisplayName())
					.add("{size}", String.valueOf(game.getParticipants().size()))
					.add("{maximum}", String.valueOf(game.getMaximumPlayers()))
					.send(onlinePlayer);
		}

		int chosenMapVotes = 0;

		for (Map.Entry<EventGameMap, EventGameMapVoteData> entry : game.getVotesData().entrySet()) {
			if (game.getGameMap() == null) {
				game.setGameMap(entry.getKey());
				chosenMapVotes = entry.getValue().getPlayers().size();
			} else {
				if (entry.getValue().getPlayers().size() >= chosenMapVotes) {
					game.setGameMap(entry.getKey());
					chosenMapVotes = entry.getValue().getPlayers().size();
				}
			}
		}

		for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
			for (GamePlayer gamePlayer : participant.getPlayers()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					PlayerUtil.reset(player);
					player.teleport(game.getGameMap().getSpectatorPoint());
					Hotbar.giveHotbarItems(player);
				}
			}
		}
	}

	@Override
	public boolean canStartEvent() {
		return game.getRemainingParticipants() >= 2;
	}

	@Override
	public void preEndEvent() {
		for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
			if (!participant.isEliminated()) {
				winningParticipant = participant;
				break;
			}
		}

		if (winningParticipant != null) {
			for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
				Profile profile = Profile.get(onlinePlayer.getUniqueId());
				new MessageFormat(me.hcfalerts.practice.Locale.EVENT_FINISH.format(profile.getLocale()))
						.add("{event_name}", game.getEvent().getName())
						.add("{event_displayname}", game.getEvent().getDisplayName())
						.add("{winner}", winningParticipant.getConjoinedNames())
						.add("{context}", (winningParticipant.getPlayers().size() == 1 ? "has" : "have"))
						.send(onlinePlayer);
			}
		}
	}

	@Override
	public void endEvent() {
		EventGame.setActiveGame(null);
		EventGame.setCooldown(new Cooldown(30_000L));

		for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
			for (GamePlayer gamePlayer : participant.getPlayers()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					Profile profile = Profile.get(player.getUniqueId());
					profile.setState(ProfileState.LOBBY);

					Hotbar.giveHotbarItems(player);
					HCFPractice.get().getEssentials().teleportToSpawn(player);
					VisibilityLogic.handle(player);
				}
			}
		}

		for (Profile value : Profile.getProfiles().values()) {
			if (value.getPlayer() != null && value.isOnline() && value.getState() == ProfileState.LOBBY) {
				Hotbar.giveHotbarItems(value.getPlayer());
			}
		}
	}

	@Override
	public boolean canEndEvent() {
		return game.getRemainingParticipants() <= 1;
	}

	@Override
	public void cancelEvent() {
		game.sendMessage(ChatColor.DARK_RED + "The event has been cancelled by an administrator!");

		EventGame.setActiveGame(null);
		EventGame.setCooldown(new Cooldown(30_000L));

		for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
			for (GamePlayer gamePlayer : participant.getPlayers()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					Profile profile = Profile.get(player.getUniqueId());
					profile.setState(ProfileState.LOBBY);

					Hotbar.giveHotbarItems(player);

					HCFPractice.get().getEssentials().teleportToSpawn(player);
				}
			}
		}

		for (Profile value : Profile.getProfiles().values()) {
			if (value.getPlayer() != null && value.isOnline() && value.getState() == ProfileState.LOBBY) {
				Hotbar.giveHotbarItems(value.getPlayer());
			}
		}
	}

	@Override
	public void preStartRound() {
		roundNumber++;

		GameParticipant<GamePlayer>[] participants = findParticipants();
		participantA = participants[0];
		participantB = participants[1];

		for (GamePlayer gamePlayer : participantA.getPlayers()) {
			if (!gamePlayer.isDisconnected()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					Profile profile = Profile.get(player.getUniqueId());
					new MessageFormat(me.hcfalerts.practice.Locale.EVENT_ROUND_OPPONENT.format(profile.getLocale()))
						.add("{context}", (participantB.getPlayers().size() == 1 ? "" : "s"))
						.add("{name}", participantB.getConjoinedNames())
						.send(player);

					player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
				}
			}
		}

		for (GamePlayer gamePlayer : participantB.getPlayers()) {
			if (!gamePlayer.isDisconnected()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					Profile profile = Profile.get(player.getUniqueId());
					new MessageFormat(me.hcfalerts.practice.Locale.EVENT_ROUND_OPPONENT.format(profile.getLocale()))
						.add("{context}", (participantA.getPlayers().size() == 1 ? "" : "s"))
						.add("{name}", participantA.getConjoinedNames())
						.send(player);

					player.playSound(player.getLocation(), Sound.ENDERDRAGON_GROWL, 1.0F, 1.0F);
				}
			}
		}
	}

	@Override
	public void startRound() {
		game.sendMessage(me.hcfalerts.practice.Locale.EVENT_ROUND_START, new MessageFormat()
			.add("{round}", String.valueOf(game.getGameLogic().getRoundNumber()))
			.add("{participant_a}", participantA.getConjoinedNames())
			.add("{participant_b}", participantB.getConjoinedNames())
		);

		game.sendSound(Sound.ORB_PICKUP, 1.0F, 15F);

		game.getGameMap().teleportFighters(game);

		for (GameParticipant<GamePlayer> participant : new GameParticipant[]{ participantA, participantB }) {
			for (GamePlayer gamePlayer : participant.getPlayers()) {
				Player player = gamePlayer.getPlayer();

				if (player != null) {
					player.getInventory().setArmorContents(new ItemStack[4]);
					player.getInventory().setContents(new ItemStack[36]);
					player.updateInventory();
				}
			}
		}
	}

	@Override
	public boolean canStartRound() {
		return game.getRemainingParticipants() >= 2;
	}

	@Override
	public void endRound() {
		GameParticipant<GamePlayer> loser = getLosingParticipant();

		game.sendMessage(me.hcfalerts.practice.Locale.EVENT_ROUND_ELIMINATION, new MessageFormat()
			.add("{loser_name}", loser.getConjoinedNames())
			.add("{context}", loser.getPlayers().size() == 1 ? "was" : "were")
		);

		for (GamePlayer gamePlayer : participantA == loser ? participantB.getPlayers() : participantA.getPlayers()) {
			Player player = gamePlayer.getPlayer();

			if (player != null) {
				PlayerUtil.reset(player);
				Hotbar.giveHotbarItems(player);
				player.teleport(game.getGameMap().getSpectatorPoint());
				VisibilityLogic.handle(gamePlayer.getPlayer());
			}
		}
	}

	@Override
	public boolean canEndRound() {
		return (participantA != null && participantA.isAllDead()) ||
		       (participantB != null && participantB.isAllDead());
	}

	@Override
	public void onVote(Player player, EventGameMap gameMap) {
		if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
		    game.getGameState() == EventGameState.STARTING_EVENT) {
			EventGameMapVoteData voteData = game.getVotesData().get(gameMap);

			if (voteData != null) {
				if (voteData.hasVote(player.getUniqueId())) {
					player.sendMessage(ChatColor.RED + "You have already voted for that map!");
				} else {
					for (EventGameMapVoteData otherVoteData : game.getVotesData().values()) {
						if (otherVoteData.hasVote(player.getUniqueId())) {
							otherVoteData.getPlayers().remove(player.getUniqueId());
						}
					}

					voteData.addVote(player.getUniqueId());

					game.sendMessage(me.hcfalerts.practice.Locale.EVENT_PLAYER_VOTE, new MessageFormat()
						.add("{player_name}", HCFPractice.get().getRankManager().getRank().getPrefix(player.getUniqueId()) + player.getName())
						.add("{map_name}", gameMap.getMapName())
						.add("{votes}", String.valueOf(voteData.getPlayers().size()))
					);
				}
			} else {
				player.sendMessage(ChatColor.RED + "A map with that name does not exist.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "The event has already started.");
		}
	}

	@Override
	public void onJoin(Player player) {
		game.getParticipants().add(new GameParticipant<>(new GamePlayer(player.getUniqueId(), player.getName())));

		game.sendMessage(me.hcfalerts.practice.Locale.EVENT_PLAYER_JOIN, new MessageFormat()
			.add("{player_name}", HCFPractice.get().getRankManager().getRank().getPrefix(player.getUniqueId()) + player.getName())
			.add("{size}", String.valueOf(game.getParticipants().size()))
			.add("{maximum}", String.valueOf(game.getMaximumPlayers()))
		);

		Profile profile = Profile.get(player.getUniqueId());
		profile.setState(ProfileState.EVENT);

		Hotbar.giveHotbarItems(player);

		for (Map.Entry<EventGameMap, EventGameMapVoteData> entry : game.getVotesData().entrySet()) {
			ItemStack itemStack = Hotbar.getItems().get(HotbarItem.MAP_SELECTION).getItemStack().clone();
			ItemMeta itemMeta = itemStack.getItemMeta();

			itemMeta.setDisplayName(itemMeta.getDisplayName().replace("%MAP%", entry.getKey().getMapName()));
			itemStack.setItemMeta(itemMeta);

			player.getInventory().addItem(itemStack);
		}

		player.updateInventory();
		player.teleport(game.getEvent().getLobbyLocation().clone().add(0, 2, 0));

		VisibilityLogic.handle(player);

		for (GameParticipant<GamePlayer> gameParticipant : game.getParticipants()) {
			for (GamePlayer gamePlayer : gameParticipant.getPlayers()) {
				if (!gamePlayer.isDisconnected()) {
					Player bukkitPlayer = gamePlayer.getPlayer();

					if (bukkitPlayer != null) {
						VisibilityLogic.handle(bukkitPlayer, player);
					}
				}
			}
		}
	}


	@Override
	public void onLeave(Player player) {
		if (isPlaying(player)) onDeath(player, null);

		Iterator<GameParticipant<GamePlayer>> iterator = game.getParticipants().iterator();

		while (iterator.hasNext()) {
			GameParticipant<GamePlayer> participant = iterator.next();

			if (participant.containsPlayer(player.getUniqueId())) {
				iterator.remove();

				for (GamePlayer gamePlayer : participant.getPlayers()) {
					if (!gamePlayer.isDisconnected()) {
						Player bukkitPlayer = gamePlayer.getPlayer();

						if (bukkitPlayer != null) {
							if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
							    game.getGameState() == EventGameState.STARTING_EVENT) {
								game.sendMessage(Locale.EVENT_PLAYER_LEAVE, new MessageFormat()
									.add("{player_name}", HCFPractice.get().getRankManager().getRank().getPrefix(player.getUniqueId()) + player.getName())
									.add("{remaining}", String.valueOf(game.getRemainingPlayers()))
									.add("{maximum}", String.valueOf(game.getMaximumPlayers()))
								);
							}

							Profile profile = Profile.get(bukkitPlayer.getUniqueId());
							profile.setState(ProfileState.LOBBY);

							Hotbar.giveHotbarItems(bukkitPlayer);
							VisibilityLogic.handle(bukkitPlayer, player);

							HCFPractice.get().getEssentials().teleportToSpawn(bukkitPlayer);
						}
					}
				}
			}
		}

		VisibilityLogic.handle(player);
	}

	@Override
	public void onMove(Player player) {
		if (isPlaying(player)) {
			GamePlayer gamePlayer = game.getGamePlayer(player);

			if (gamePlayer != null) {
				if (BlockUtil.isOnLiquid(player.getLocation(), 0)) {
					if (!gamePlayer.isDead()) onDeath(player, null);
				}
			}
		}
	}

	@Override
	public void onDeath(Player player, Player killer) {
		if (EventGame.getActiveGame().getGameState() == EventGameState.STARTING_EVENT) return;
		GamePlayer deadGamePlayer = game.getGamePlayer(player);
		Knockback.getKnockbackProfiler().setKnockback(player, "default");

		if (deadGamePlayer != null) {
			deadGamePlayer.setDead(true);
		}

		player.teleport(game.getGameMap().getSpectatorPoint());
		PlayerUtil.reset(player);
		Hotbar.giveHotbarItems(player);
		VisibilityLogic.handle(player);
		player.setAllowFlight(true);
		player.setFlying(true);

		if (participantA.isAllDead() || participantB.isAllDead()) {
			GameParticipant<GamePlayer> winner = getWinningParticipant();
			winner.reset();

			GameParticipant<GamePlayer> loser = getLosingParticipant();
			loser.setEliminated(true);

			if (canEndEvent()) {
				preEndEvent();
				game.setGameState(EventGameState.ENDING_EVENT);
				logicTask.setNextAction(3);
			} else if (canEndRound()) {
				game.setGameState(EventGameState.ENDING_ROUND);
				logicTask.setNextAction(1);
			}
		}
	}

	@Override
	public void onInteract(PlayerInteractEvent event, Player player, ItemStack target) {

	}

	@Override
	public void onEntityDamageByPlayer(EntityDamageByEntityEvent event, Player player, Player target) {
		event.setDamage(0);
	}

	@Override
	public void onEntityDamage(EntityDamageEvent event, Player player) {
		event.setDamage(0);
	}

	@Override
	public void onInventoryClick(InventoryClickEvent event, Player player) {
		event.setCancelled(true);
	}

	@Override
	public boolean isPlaying(Player player) {
		return (participantA != null && participantA.containsPlayer(player.getUniqueId())) ||
		       (participantB != null && participantB.containsPlayer(player.getUniqueId()));
	}

	@Override
	public List<String> getScoreboardEntries() {
		List<String> lines = new ArrayList<>();
		BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();
		for (String s : config.getStringList("EVENTS.SUMO.LINES")) {
			lines.add(s
					.replace("{event-name}", game.getEvent().getName())
					.replace("{event-displayname}", game.getEvent().getDisplayName())
					.replace("{players}", String.valueOf(game.getRemainingPlayers()))
					.replace("{max-players}", String.valueOf(game.getMaximumPlayers()))
					.replace("{bars}", CC.SB_BAR));
		}

		if (game.getGameState() == EventGameState.STARTING_ROUND ||
				game.getGameState() == EventGameState.PLAYING_ROUND ||
				game.getGameState() == EventGameState.ENDING_ROUND) {
			for (String s : config.getStringList("EVENTS.SUMO.ROUND")) {
				lines.add(s
						.replace("{round}", String.valueOf(roundNumber))
						.replace("{bars}", CC.SB_BAR));
			}
		}

		switch (game.getGameState()) {
			case WAITING_FOR_PLAYERS: {
				lines.addAll(config.getStringList("EVENTS.SUMO.WAITING-FOR-PLAYERS"));
			}
			break;
			case STARTING_EVENT: {
				for (String s : config.getStringList("EVENTS.SUMO.STARTING-EVENT")) {
					lines.add(s
							.replace("{time}", String.valueOf(game.getGameLogic().getGameLogicTask().getNextActionTime()))
							.replace("{bars}", CC.SB_BAR));
				}
			}
			break;
			case PLAYING_ROUND: {
				for (String s : config.getStringList("EVENTS.SUMO.PLAYING-ROUND")) {
					lines.add(s
							.replace("{bars}", CC.SB_BAR)
							.replace("{playerA}", participantA.getConjoinedNames())
							.replace("{playerB}", participantB.getConjoinedNames()));
				}
			}
			break;
			case STARTING_ROUND: {
				for (String s : config.getStringList("EVENTS.SUMO.STARTING-ROUND")) {
					lines.add(s
							.replace("{time}", String.valueOf(game.getGameLogic().getGameLogicTask().getNextActionTime()))
							.replace("{bars}", CC.SB_BAR));
				}
			}
			break;
			case ENDING_ROUND: {
				for (String s : config.getStringList("EVENTS.SUMO.ENDING-ROUND")) {
					lines.add(s.replace("{bars}", CC.SB_BAR));
				}
			}
			break;
			case ENDING_EVENT: {
				if (winningParticipant != null) {
					for (String s : config.getStringList("EVENTS.SUMO.ENDING-EVENT")) {
						lines.add(s
								.replace("{rounds}", String.valueOf(roundNumber))
								.replace("{bars}", CC.SB_BAR)
								.replace("{winner}", winningParticipant.getConjoinedNames()));
					}
				}
			}
			break;
		}

		if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
				game.getGameState() == EventGameState.STARTING_EVENT) {
			for (String s : config.getStringList("EVENTS.SUMO.MAP-VOTES")) {
				if (s.contains("{votes-format}")) {
					game.getVotesData().forEach((map, voteData) -> {
						lines.add(config.getString("EVENTS.SUMO.VOTES-FORMAT")
								.replace("{map-name}", map.getMapName())
								.replace("{size}", String.valueOf(voteData.getPlayers().size())));
					});
					continue;
				}
				lines.add(s.replace("{bars}", CC.SB_BAR));
			}
		}

		return lines;
	}

	private GameParticipant<GamePlayer>[] findParticipants() {
		List<GameParticipant<GamePlayer>> participants = Lists.newArrayList();

		for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
			if (!participant.isEliminated()) {
				participants.add(participant);
			}
		}
		participants.sort(Comparator.comparingInt(GameParticipant::getRoundWins));

		if (participants.size() <= 1) {
			return null;
		}

		GameParticipant<GamePlayer>[] array = new GameParticipant[] {
				participants.get(0),
				participants.get(1)
		};

		int grabFromIndex = 2;

		if (array[0].equals(participantA) && participants.size() > grabFromIndex) {
			array[0] = participants.get(grabFromIndex++);
		}

		if (array[0].equals(participantB) && participants.size() > grabFromIndex) {
			array[0] = participants.get(grabFromIndex++);
		}

		if (array[1].equals(participantA) && participants.size() > grabFromIndex) {
			array[1] = participants.get(grabFromIndex++);
		}

		if (array[1].equals(participantB) && participants.size() > grabFromIndex) {
			array[1] = participants.get(grabFromIndex++);
		}

		return array;
	}

	private GameParticipant<GamePlayer> getWinningParticipant() {
		if (participantA.isAllDead()) {
			return participantB;
		} else {
			return participantA;
		}
	}

	private GameParticipant<GamePlayer> getLosingParticipant() {
		if (participantA.isAllDead()) {
			return participantA;
		} else {
			return participantB;
		}
	}

}
