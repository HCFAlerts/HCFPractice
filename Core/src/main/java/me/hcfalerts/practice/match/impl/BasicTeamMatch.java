package me.hcfalerts.practice.match.impl;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.MatchState;
import me.hcfalerts.practice.match.mongo.MatchInfo;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.party.classes.HCFClass;
import me.hcfalerts.practice.party.classes.bard.BardEnergyTask;
import me.hcfalerts.practice.party.classes.rogue.RogueClass;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.follow.Follow;
import me.hcfalerts.practice.profile.meta.ProfileKitData;
import me.hcfalerts.practice.profile.meta.ProfileRematchData;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.scoreboard.ability.PandaAbility;
import me.hcfalerts.practice.utilities.BukkitReflection;
import me.hcfalerts.practice.utilities.KitUtils;
import me.hcfalerts.practice.utilities.PlayerUtil;
import me.hcfalerts.practice.utilities.chat.ChatComponentBuilder;
import me.hcfalerts.practice.utilities.elo.EloUtil;
import me.hcfalerts.practice.utilities.file.languaje.Lang;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import me.hcfalerts.practice.utilities.string.TimeUtil;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.github.paperspigot.Title;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static me.hcfalerts.practice.utilities.string.BridgeUtils.getStringPoint;

@Getter
public class BasicTeamMatch extends Match {

	private final GameParticipant<MatchGamePlayer> participantA;
	private final GameParticipant<MatchGamePlayer> participantB;
	private @Setter GameParticipant<MatchGamePlayer> winningParticipant;
	private @Setter GameParticipant<MatchGamePlayer> losingParticipant;

	public BasicTeamMatch(Queue queue, Kit kit, Arena arena, boolean ranked, GameParticipant<MatchGamePlayer> participantA,
                          GameParticipant<MatchGamePlayer> participantB) {
		super(queue, kit, arena, ranked);

		this.participantA = participantA;
		this.participantB = participantB;
	}

	@Override
	public void setupPlayer(Player player) {
		super.setupPlayer(player);
		Profile profile = Profile.get(player.getUniqueId());

		Party party = profile.getParty();
		if (party != null) {
			if (kit.getGameRules().isHcf()) {
				Kit kit;
				try {
					if (party.getArchers().contains(player.getUniqueId())) {
						kit = Kit.getByName("Archer");
						player.getInventory().setArmorContents(Objects.requireNonNull(kit).getKitLoadout().getArmor());
						player.getInventory().setContents(kit.getKitLoadout().getContents());
					} else if (party.getBards().contains(player.getUniqueId())) {
						kit = Kit.getByName("Bard");
						player.getInventory().setArmorContents(Objects.requireNonNull(kit).getKitLoadout().getArmor());
						player.getInventory().setContents(kit.getKitLoadout().getContents());
					} else if (party.getRogues().contains(player.getUniqueId())) {
						kit = Kit.getByName("Rogue");
						player.getInventory().setArmorContents(Objects.requireNonNull(kit).getKitLoadout().getArmor());
						player.getInventory().setContents(kit.getKitLoadout().getContents());
					}
				} catch (Exception e) {
					throw new IllegalArgumentException("Kit \"Archer\" or \"Bard\" or \"Rogue\" doesn't exist");
				}
			}
		}

		if (kit.getGameRules().isHcfTrap()) {
			try {
				Kit trapper = Kit.getByName("Trapper");
				for (MatchGamePlayer participantAPlayer : participantA.getPlayers()) {
					participantAPlayer.getPlayer().getInventory().setArmorContents(trapper.getKitLoadout().getArmor());
					participantAPlayer.getPlayer().getInventory().setContents(trapper.getKitLoadout().getContents());
				}
				Kit raider = Kit.getByName("Raider");
				for (MatchGamePlayer participantBPlayer : participantB.getPlayers()) {
					participantBPlayer.getPlayer().getInventory().setArmorContents(raider.getKitLoadout().getArmor());
					participantBPlayer.getPlayer().getInventory().setContents(raider.getKitLoadout().getContents());
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Kit \"Trapper\" or \"Raider\" doesn't exist");
			}
		}

		if (kit.getGameRules().isBridge()) {
			ProfileKitData kitData = profile.getKitData().get(kit);
			if (kitData.getKitCount() == 0) {
				player.getInventory().setContents(kit.getKitLoadout().getContents());
				KitUtils.giveBridgeKit(player);
			}
		}

		player.updateInventory();

		// Teleport the player to their spawn point
		Location spawn;
		if (this.getKit().getGameRules().isParkour()) {
			spawn = getArena().getSpawnA();
		} else {
			spawn = participantA.containsPlayer(player.getUniqueId()) ?
					getArena().getSpawnA() : getArena().getSpawnB();
		}
		if (spawn.getBlock().getType() == Material.AIR) player.teleport(spawn);
		else player.teleport(spawn.add(0, 2, 0));

		Follow follow = Follow.getByFollowed(player.getUniqueId());
		if (follow != null) {
			if (follow.getFollower() != null) addSpectator(Bukkit.getPlayer(follow.getFollower()), player, true);
		}
	}

	@Override
	public void end() {
		if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
			UUID rematchKey = UUID.randomUUID();

			for (GameParticipant<MatchGamePlayer> gameParticipant : getParticipants()) {
				for (MatchGamePlayer gamePlayer : gameParticipant.getPlayers()) {
					if (!gamePlayer.isDisconnected()) {
						Profile profile = Profile.get(gamePlayer.getUuid());

						if (winningParticipant.containsPlayer(gamePlayer.getUuid()) && !gamePlayer.isDead() && profile.getDeathEffect() != null) {
							profile.getDeathEffect().stop();
						}

						if (profile.getParty() == null) {
							if (gamePlayer.getPlayer() == null) {
								super.end();
								return;
							}
							UUID opponent;

							if (gameParticipant.equals(participantA)) opponent = participantB.getLeader().getUuid();
							else opponent = participantA.getLeader().getUuid();

							if (opponent != null) {
								int rounds = 1;
								if (this instanceof BasicTeamRoundMatch) {
									rounds = ((BasicTeamRoundMatch) this).getRoundsToWin();
								}
								ProfileRematchData rematchData = new ProfileRematchData(rematchKey,
										gamePlayer.getUuid(), opponent, kit, arena, rounds);
								profile.setRematchData(rematchData);
							}

							RogueClass.getLastJumpUsage().remove(profile.getPlayer().getName());
							RogueClass.getLastSpeedUsage().remove(profile.getPlayer().getName());
							RogueClass.getBackstabCooldown().remove(profile.getPlayer().getName());
						}
					}
				}
			}
		}

		super.end();
	}

	@Override
	public boolean canEndMatch() {
		return true;
	}

	@Override
	public boolean canStartRound() {
		if (kit.getGameRules().isSumo()) {
			if (ranked) {
				return !(participantA.getRoundWins() == 3 || participantB.getRoundWins() == 3);
			}
		}
		return kit.getGameRules().isBridge();
	}

	@Override
	public void onRoundEnd() {
		// Store winning participant
		winningParticipant = participantA.isAllDead() ? participantB : participantA;

		// Store losing participant
		losingParticipant = participantA.isAllDead() ? participantA : participantB;
		losingParticipant.setEliminated(true);

		// Send Death Effects
		for (MatchGamePlayer player : winningParticipant.getPlayers()) {
			if (!player.isDisconnected() && !player.isDead() && Profile.get(player.getUuid()).getDeathEffect() != null) {
				Profile.get(player.getUuid()).getDeathEffect().apply();
			}
		}

		if (canEndMatch()) {
			for (MatchGamePlayer player : winningParticipant.getPlayers()) {
				Profile profile = Profile.get(player.getUuid());
				if (player.getPlayer() != null) {
					player.getPlayer().sendTitle(new Title(
							new MessageFormat(Locale.MATCH_WINNER_TITLE.format(profile.getLocale())).toString(),
							new MessageFormat(Locale.MATCH_WINNER_SUBTITLE.format(profile.getLocale()))
									.add("{winner}", ChatColor.stripColor(winningParticipant.getLeader().getUsername())).toString(),
							20, 40, 20));
				}
			}
			for (MatchGamePlayer player : losingParticipant.getPlayers()) {
				Profile profile = Profile.get(player.getUuid());
				if (player.getPlayer() != null) {
					player.getPlayer().sendTitle(new Title(
							new MessageFormat(Locale.MATCH_LOSER_TITLE.format(profile.getLocale())).toString(),
							new MessageFormat(Locale.MATCH_LOSER_SUBTITLE.format(profile.getLocale()))
									.add("{winner}", ChatColor.stripColor(winningParticipant.getLeader().getUsername())).toString(),
							20, 40, 20));
				}
			}
		}

		if (kit.getGameRules().isBridge()) {
			for (GameParticipant<MatchGamePlayer> participant : this.getParticipants()) {
				for (MatchGamePlayer player : participant.getPlayers()) {
					Player other = player.getPlayer();
					PlayerUtil.reset(other);

					Location spawn = this.getParticipantA().containsPlayer(other.getUniqueId()) ?
							this.getArena().getSpawnA() :
							this.getArena().getSpawnB();

					PlayerUtil.denyMovement(other);
					other.teleport(spawn.add(0, 2, 0));
					Profile profile = Profile.get(other.getUniqueId());
					if (profile.getSelectedKit() == null) {
						other.getInventory().setContents(kit.getKitLoadout().getContents());
					} else {
						other.getInventory().setContents(profile.getSelectedKit().getContents());
					}
					KitUtils.giveBridgeKit(other);
				}
			}
			return;
		}

		if (kit.getGameRules().isSumo()) {
			if (!canEndMatch()) {
				state = MatchState.ENDING_ROUND;
				logicTask.setNextAction(3);
				for (GameParticipant<MatchGamePlayer> participant : this.getParticipants()) {
					for (MatchGamePlayer gamePlayer : participant.getPlayers()) {
						Player player = gamePlayer.getPlayer();
						// Teleport the player to their spawn point
						Location spawn = participantA.containsPlayer(player.getUniqueId()) ?
								getArena().getSpawnA() : getArena().getSpawnB();

						if (spawn.getBlock().getType() == Material.AIR) player.teleport(spawn);
						else player.teleport(spawn.add(0, 2, 0));
						PlayerUtil.denyMovement(player);
					}
				}
				return;
			}
		}

		// Set opponents in snapshots if solo
		if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
			Profile winner = Profile.get(winningParticipant.getLeader().getUuid());
			Profile loser = Profile.get(losingParticipant.getLeader().getUuid());

			winner.addWinStreak();
			if (winner.getPlayer() != null) {
				new MessageFormat(Locale.MATCH_WIN_STREAK_MESSAGE.format(winner.getLocale()))
						.add("{streak}", String.valueOf(winner.getWinStreak()))
						.add("{context}", loser.getWinStreak() == 1 ? "" : "s")
						.send(winner.getPlayer());
			}

			if (loser.getPlayer() != null && loser.getWinStreak() > 0) {
				new MessageFormat(Locale.MATCH_LOSE_STREAK_MESSAGE.format(loser.getLocale()))
						.add("{streak}", String.valueOf(loser.getWinStreak()))
						.add("{context}", loser.getWinStreak() == 1 ? "" : "s")
						.send(loser.getPlayer());
			}
			loser.resetWinStreak();

			if (ranked) {
				int oldWinnerElo = winningParticipant.getLeader().getElo();
				int oldLoserElo = losingParticipant.getLeader().getElo();

				int newWinnerElo = EloUtil.getNewRating(oldWinnerElo, oldLoserElo, true);
				int newLoserElo = EloUtil.getNewRating(oldLoserElo, oldWinnerElo, false);

				winningParticipant.getLeader().setEloMod(newWinnerElo - oldWinnerElo);
				losingParticipant.getLeader().setEloMod(oldLoserElo - newLoserElo);

				Profile winningProfile = Profile.get(winningParticipant.getLeader().getUuid());
				winningProfile.getKitData().get(kit).setElo(newWinnerElo);
				winningProfile.getKitData().get(kit).incrementWon();

				Profile losingProfile = Profile.get(losingParticipant.getLeader().getUuid());
				losingProfile.getKitData().get(kit).setElo(newLoserElo);
				losingProfile.getKitData().get(kit).incrementLost();

				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
				LocalDateTime now = LocalDateTime.now();

				MatchInfo matchInfo = new MatchInfo(winningParticipant.getConjoinedNames(),
					losingParticipant.getConjoinedNames(),
					kit,
					winningParticipant.getLeader().getEloMod(),
					losingParticipant.getLeader().getEloMod(),
					dtf.format(now),
					TimeUtil.millisToTimer(System.currentTimeMillis() - timeData));

				winningProfile.getMatches().add(matchInfo);
				losingProfile.getMatches().add(matchInfo);
			}
		}

		super.onRoundEnd();
	}

	@Override
	public boolean canEndRound() {
		return participantA.isAllDead() || participantB.isAllDead();
	}

	@Override
	public boolean isOnSameTeam(Player first, Player second) {
		boolean[] booleans = new boolean[]{
				participantA.containsPlayer(first.getUniqueId()),
				participantB.containsPlayer(first.getUniqueId()),
				participantA.containsPlayer(second.getUniqueId()),
				participantB.containsPlayer(second.getUniqueId())
		};

		return (booleans[0] && booleans[2]) || (booleans[1] && booleans[3]);
	}

	@Override
	public List<GameParticipant<MatchGamePlayer>> getParticipants() {
		return Arrays.asList(participantA, participantB);
	}

	@Override
	public ChatColor getRelationColor(Player viewer, Player target) {
		if (kit.getGameRules().isBridge()) {
			if (participantA.containsPlayer(target.getUniqueId())) return ChatColor.RED;
			else return ChatColor.BLUE;
		}

		if (viewer.equals(target)) return ChatColor.GREEN;

		boolean[] booleans = new boolean[]{
				participantA.containsPlayer(viewer.getUniqueId()),
				participantB.containsPlayer(viewer.getUniqueId()),
				participantA.containsPlayer(target.getUniqueId()),
				participantB.containsPlayer(target.getUniqueId())
		};

		if ((booleans[0] && booleans[3]) || (booleans[2] && booleans[1])) return ChatColor.RED;
		else if ((booleans[0] && booleans[2]) || (booleans[1] && booleans[3])) return ChatColor.GREEN;
		else if (spectators.contains(viewer.getUniqueId())) return participantA.containsPlayer(target.getUniqueId()) ?
					ChatColor.GREEN : ChatColor.RED;
		else return ChatColor.YELLOW;
	}

	@Override
	public List<String> getScoreboardLines(Player player) {
		List<String> lines = new ArrayList<>();
		BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

		if (getParticipant(player) != null) {
			if (state == MatchState.STARTING_ROUND || state == MatchState.PLAYING_ROUND || state == MatchState.ENDING_ROUND || state == MatchState.ENDING_MATCH) {
				if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
					GameParticipant<MatchGamePlayer> yours = getParticipant(player);
					GameParticipant<MatchGamePlayer> opponent = participantA.equals(yours) ?
							participantB : participantA;

					Profile opponentProfile = Profile.get(opponent.getLeader().getUuid());
					Profile yoursProfile = Profile.get(yours.getLeader().getUuid());

					if (state == MatchState.ENDING_MATCH) {
						for (String s : config.getStringList("BOARD.MATCH.1V1.MATCH_END")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{opponent}", opponent.getLeader().getPlayer().getName())
									.replace("{opponent-color}", opponentProfile.getColor()));
						}
						return lines;
					}

					if (kit.getGameRules().isBoxing()) {
						String actualHits = "0";
						if ((yours.getLeader().getHits() - opponent.getLeader().getHits()) > 0) {
							actualHits = "+" + (yours.getLeader().getHits() - opponent.getLeader().getHits());
						}
						else if ((yours.getLeader().getHits() - opponent.getLeader().getHits()) < 0) {
							actualHits = String.valueOf(yours.getLeader().getHits() - opponent.getLeader().getHits());
						}
						for (String s : config.getStringList("BOARD.MATCH.1V1.BOXING_MODE")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{opponent-color}", opponentProfile.getColor())
									.replace("{opponent}", opponent.getLeader().getPlayer().getName())
									.replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
									.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{hits}", actualHits)
									.replace("{your-hits}", String.valueOf(yours.getLeader().getHits()))
									.replace("{opponent-hits}", String.valueOf(opponent.getLeader().getHits()))
									.replace("{combo}", String.valueOf(yours.getLeader().getCombo())));
						}
						return lines;
					}
					else if (kit.getGameRules().isBridge()) {
						for (String s : config.getStringList("BOARD.MATCH.1V1.BRIDGE.LINES")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{opponent-color}", opponentProfile.getColor())
									.replace("{opponent}", opponent.getLeader().getPlayer().getName())
									.replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
									.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, 1))
									.replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, 1))
									.replace("{kills}", String.valueOf(getParticipant(player).getLeader().getKills())));
						}
						return lines;
					}
					else if (kit.getGameRules().isHcfTrap()) {
						for (String s : config.getStringList("BOARD.MATCH.1V1.HCF_TRAP")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{opponent-color}", opponentProfile.getColor())
									.replace("{opponent}", opponent.getLeader().getPlayer().getName())
									.replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
									.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName()));
						}
						lines.addAll(PandaAbility.getScoreboardLines(player));
						return lines;
					}
					else if (kit.getGameRules().isHcf() && yoursProfile.getParty() != null) {
						for (String s : config.getStringList("BOARD.MATCH.1V1.HCF.LINES")) {
							if (s.contains("{bard-energy}")) {
								if (yoursProfile.getParty().getBards().contains(player.getUniqueId())) {
									for (String s1 : config.getStringList("BOARD.MATCH.1V1.HCF.BARD_ENERGY")) {
										lines.add(s1
												.replace("{energy}", String.valueOf(BardEnergyTask.getEnergy().get(player.getUniqueId()).intValue())));
									}
								}
								continue;
							}
							lines.add(s.replace("{duration}", getDuration())
									.replace("{opponent-color}", opponentProfile.getColor())
									.replace("{opponent}", opponent.getLeader().getPlayer().getName())
									.replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
									.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{class}", HCFClass.classMap.get(player.getUniqueId()) != null ?
											HCFClass.classMap.get(player.getUniqueId()).getName() : "Diamond"));
						}
						lines.addAll(PandaAbility.getScoreboardLines(player));
						return lines;
					}
					else if (kit.getGameRules().isParkour()) {
						for (String s : config.getStringList("BOARD.MATCH.1V1.PARKOUR")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
									.replace("{player}", player.getName())
									.replace("{player-checkpoints}", String.valueOf(yours.getLeader().getCheckPoints()))
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{rival}", opponent.getLeader().getPlayer().getName())
									.replace("{rival-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
									.replace("{rival-checkpoints}", String.valueOf(opponent.getLeader().getCheckPoints())));
						}
						return lines;
					} else {
						for (String s : config.getStringList("BOARD.MATCH.1V1.DEFAULT")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{opponent-color}", opponentProfile.getColor())
									.replace("{opponent}", opponent.getLeader().getPlayer().getName())
									.replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
									.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName()));
						}
						return lines;
					}
				} else {
					GameParticipant<MatchGamePlayer> friendly = getParticipant(player);
					GameParticipant<MatchGamePlayer> opponent = participantA.equals(friendly) ?
							participantB : participantA;

					Profile yourProfile = Profile.get(player.getUniqueId());

					if (participantA.getPlayers().size() != 1 || participantB.getPlayers().size() != 1) {
						if (state == MatchState.ENDING_MATCH) {
							for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.MATCH_END")) {
								lines.add(s.replace("{duration}", getDuration())
										.replace("{arena-author}", getArena().getAuthor())
										.replace("{kit}", kit.getName()));
							}
							return lines;
						}

						if (kit.getGameRules().isBoxing()) {
							String actualHits = "0";
							int friendTotalHits = 0;
							int opponentTotalHits = 0;
							for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
								friendTotalHits += friendlyPlayer.getHits();
							}
							for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
								opponentTotalHits += enemyPlayer.getHits();
							}
							if ((friendTotalHits - opponentTotalHits) > 0) {
								actualHits = "+" + (friendTotalHits - opponentTotalHits);
							}
							else if ((friendTotalHits - opponentTotalHits) < 0) {
								actualHits = String.valueOf(friendTotalHits - opponentTotalHits);
							}
							for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.BOXING_MODE")) {
								lines.add(s.replace("{duration}", getDuration())
										.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
										.replace("{arena-author}", getArena().getAuthor())
										.replace("{kit}", kit.getName())
										.replace("{team-hits}", actualHits)
										.replace("{hits}", String.valueOf(getGamePlayer(player).getHits()))
										.replace("{rival-hits}", String.valueOf(opponentTotalHits))
										.replace("{combo}", String.valueOf(getGamePlayer(player).getCombo()))
										.replace("{rivals}", String.valueOf(opponent.getPlayers().size())));
							}
							return lines;
						}
						else if (kit.getGameRules().isBridge()) {
							for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.BRIDGE.LINES")) {
								lines.add(s.replace("{duration}", getDuration())
										.replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
										.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
										.replace("{arena-author}", getArena().getAuthor())
										.replace("{kit}", kit.getName())
										.replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, 1))
										.replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, 1))
										.replace("{kills}", String.valueOf(getParticipant(player).getLeader().getKills())));
							}
							return lines;
						}
						else if (kit.getGameRules().isHcfTrap()) {
							int friendAlive = 0;
							int rivalsAlive = 0;
							for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
								if (!friendlyPlayer.isDead()) {
									friendAlive++;
								}
							}
							for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
								if (!enemyPlayer.isDead()) {
									rivalsAlive++;
								}
							}
							for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.HCF_TRAP")) {
								lines.add(s.replace("{duration}", getDuration())
										.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
										.replace("{arena-author}", getArena().getAuthor())
										.replace("{kit}", kit.getName())
										.replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
										.replace("{rivals-alive}", String.valueOf(rivalsAlive))
										.replace("{yours}", String.valueOf(friendly.getPlayers().size()))
										.replace("{yours-alive}", String.valueOf(friendAlive)));
							}
							lines.addAll(PandaAbility.getScoreboardLines(player));
							return lines;
						}
						else if (kit.getGameRules().isHcf() && yourProfile.getParty() != null) {
							int friendAlive = 0;
							int rivalsAlive = 0;
							for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
								if (!friendlyPlayer.isDead()) {
									friendAlive++;
								}
							}
							for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
								if (!enemyPlayer.isDead()) {
									rivalsAlive++;
								}
							}
							for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.HCF.LINES")) {
								if (s.contains("{bard-energy}")) {
									if (yourProfile.getParty().getBards().contains(player.getUniqueId())) {
										for (String s1 : config.getStringList("BOARD.MATCH.MASS_FIGHT.HCF.BARD_ENERGY")) {
											lines.add(s1
													.replace("{energy}", String.valueOf(BardEnergyTask.getEnergy().get(player.getUniqueId()).intValue())));
										}
									}
									continue;
								}
								lines.add(s.replace("{duration}", getDuration())
										.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
										.replace("{arena-author}", getArena().getAuthor())
										.replace("{kit}", kit.getName())
										.replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
										.replace("{rivals-alive}", String.valueOf(rivalsAlive))
										.replace("{yours}", String.valueOf(friendly.getPlayers().size()))
										.replace("{yours-alive}", String.valueOf(friendAlive))
										.replace("{class}", HCFClass.classMap.get(player.getUniqueId()) != null ?
												HCFClass.classMap.get(player.getUniqueId()).getName() : "Diamond"));
							}
							lines.addAll(PandaAbility.getScoreboardLines(player));
							return lines;
						}
						else if (kit.getGameRules().isParkour()) {
							for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.PARKOUR")) {
								lines.add(s.replace("{duration}", getDuration())
										.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
										.replace("{player}", player.getName())
										.replace("{arena-author}", getArena().getAuthor())
										.replace("{kit}", kit.getName())
										.replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
										.replace("{yours}", String.valueOf(friendly.getPlayers().size()))
										.replace("{yours-checkpoints}", String.valueOf(getGamePlayer(player).getCheckPoints())));
							}
							return lines;
						} else {
							int friendAlive = 0;
							int rivalsAlive = 0;
							for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
								if (!friendlyPlayer.isDead()) {
									friendAlive++;
								}
							}
							for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
								if (!enemyPlayer.isDead()) {
									rivalsAlive++;
								}
							}
							for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.DEFAULT")) {
								lines.add(s.replace("{duration}", getDuration())
										.replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
										.replace("{arena-author}", getArena().getAuthor())
										.replace("{kit}", kit.getName())
										.replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
										.replace("{rivals-alive}", String.valueOf(rivalsAlive))
										.replace("{yours}", String.valueOf(friendly.getPlayers().size()))
										.replace("{yours-alive}", String.valueOf(friendAlive)));
							}
							return lines;
						}
					}
				}
			}
		}

		return lines;
	}

	@Override
	public List<String> getSpectatorScoreboardLines() {
		List<String> lines = Lists.newArrayList();
		BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

		if (state == MatchState.STARTING_ROUND || state == MatchState.PLAYING_ROUND || state == MatchState.ENDING_ROUND || state == MatchState.ENDING_MATCH) {
			if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
				GameParticipant<MatchGamePlayer> second = participantA;
				GameParticipant<MatchGamePlayer> first = participantB;

				Profile firstProfile = Profile.get(first.getLeader().getUuid());
				Profile secondProfile = Profile.get(second.getLeader().getUuid());

				if (state == MatchState.ENDING_MATCH) {
					for (String s : config.getStringList("BOARD.SPECTATE.1V1.MATCH_END")) {
						lines.add(s.replace("{duration}", getDuration())
								.replace("{arena-author}", getArena().getAuthor())
								.replace("{kit}", kit.getName())
								.replace("{opponent}", second.getLeader().getPlayer().getName())
								.replace("{opponent-color}", secondProfile.getColor()));
					}
					return lines;
				}

				if (kit.getGameRules().isBoxing()) {
					String yoursHits = "0";
					String opponentHits = "0";
					if ((first.getLeader().getHits() - second.getLeader().getHits()) > 0) {
						yoursHits = "+" + (first.getLeader().getHits() - second.getLeader().getHits());
						opponentHits = String.valueOf(second.getLeader().getHits() - first.getLeader().getHits());
					}
					else if ((first.getLeader().getHits() - second.getLeader().getHits()) < 0) {
						yoursHits = String.valueOf(first.getLeader().getHits() - second.getLeader().getHits());
						opponentHits = "+" + (second.getLeader().getHits() - first.getLeader().getHits());
					}
					for (String s : config.getStringList("BOARD.SPECTATE.1V1.BOXING")) {
						lines.add(s.replace("{duration}", getDuration())
								.replace("{nd-color}", secondProfile.getColor())
								.replace("{nd-player}", second.getLeader().getPlayer().getName())
								.replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
								.replace("{nd-gap}", opponentHits)
								.replace("{nd-hits}", String.valueOf(second.getLeader().getHits()))
								.replace("{nd-combo}", String.valueOf(second.getLeader().getCombo()))
								.replace("{st-color}", firstProfile.getColor())
								.replace("{st-player}", first.getLeader().getPlayer().getName())
								.replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
								.replace("{st-gap}", yoursHits)
								.replace("{st-hits}", String.valueOf(first.getLeader().getHits()))
								.replace("{st-combo}", String.valueOf(first.getLeader().getCombo()))
								.replace("{arena-author}", getArena().getAuthor())
								.replace("{kit}", kit.getName()));
					}
					return lines;
				}
				else if (kit.getGameRules().isBridge()) {
					for (String s : config.getStringList("BOARD.SPECTATE.1V1.BRIDGE.LINES")) {
						lines.add(s.replace("{duration}", getDuration())
								.replace("{red-color}", secondProfile.getColor())
								.replace("{red-player}", second.getLeader().getPlayer().getName())
								.replace("{red-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
								.replace("{red-kills}", String.valueOf(getParticipant(second.getLeader().getPlayer()).getLeader().getKills()))
								.replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, 1))
								.replace("{blue-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
								.replace("{blue-kills}", String.valueOf(getParticipant(first.getLeader().getPlayer()).getLeader().getKills()))
								.replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, 1))
								.replace("{arena-author}", getArena().getAuthor())
								.replace("{kit}", kit.getName()));
					}
					return lines;
				}
				else if (kit.getGameRules().isHcfTrap()) {
					for (String s : config.getStringList("BOARD.SPECTATE.1V1.HCF_TRAP")) {
						lines.add(s.replace("{duration}", getDuration())
								.replace("{nd-color}", secondProfile.getColor())
								.replace("{nd-player}", second.getLeader().getPlayer().getName())
								.replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
								.replace("{st-color}", firstProfile.getColor())
								.replace("{st-player}", first.getLeader().getPlayer().getName())
								.replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
								.replace("{arena-author}", getArena().getAuthor())
								.replace("{kit}", kit.getName()));
					}
					return lines;
				}
				else if (kit.getGameRules().isHcf() && firstProfile.getParty() != null) {
					for (String s : config.getStringList("BOARD.SPECTATE.1V1.HCF.LINES")) {
						lines.add(s.replace("{duration}", getDuration())
								.replace("{nd-color}", secondProfile.getColor())
								.replace("{nd-player}", second.getLeader().getPlayer().getName())
								.replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
								.replace("{st-color}", firstProfile.getColor())
								.replace("{st-player}", first.getLeader().getPlayer().getName())
								.replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
								.replace("{arena-author}", getArena().getAuthor())
								.replace("{kit}", kit.getName()));
					}
					return lines;
				}
				else if (kit.getGameRules().isParkour()) {
					// TODO: Variable para tomar el que mas checkpoints lleve
					for (String s : config.getStringList("BOARD.SPECTATE.1V1.PARKOUR")) {
						lines.add(s.replace("{duration}", getDuration())
								.replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
								.replace("{st-player}", first.getLeader().getPlayer().getName())
								.replace("{st-checkpoints}", String.valueOf(first.getLeader().getCheckPoints()))
								.replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
								.replace("{nd-player}", second.getLeader().getPlayer().getName())
								.replace("{nd-checkpoints}", String.valueOf(second.getLeader().getCheckPoints()))
								.replace("{arena-author}", getArena().getAuthor())
								.replace("{kit}", kit.getName()));
					}
					return lines;
				} else {
					for (String s : config.getStringList("BOARD.SPECTATE.1V1.DEFAULT")) {
						lines.add(s.replace("{duration}", getDuration())
								.replace("{nd-color}", secondProfile.getColor())
								.replace("{nd-player}", second.getLeader().getPlayer().getName())
								.replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
								.replace("{st-color}", firstProfile.getColor())
								.replace("{st-player}", first.getLeader().getPlayer().getName())
								.replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
								.replace("{arena-author}", getArena().getAuthor())
								.replace("{kit}", kit.getName()));
					}
					return lines;
				}
			} else {
				GameParticipant<MatchGamePlayer> first = participantA;
				GameParticipant<MatchGamePlayer> second = participantB;

				if (getParticipantA().getPlayers().size() != 1 || getParticipantB().getPlayers().size() != 1) {
					if (state == MatchState.ENDING_MATCH) {
						for (String s : config.getStringList("BOARD.SPECTATE.1V1.MATCH_END")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{st-total}", String.valueOf(first.getPlayers().size()))
									.replace("{nd-total}", String.valueOf(second.getPlayers().size()))
									.replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
						}
						return lines;
					}

					if (kit.getGameRules().isBoxing()) {
						String yoursHits = "0";
						String opponentHits = "0";
						/*if ((first.getLeader().getHits() - second.getLeader().getHits()) > 0) {
							yoursHits = "+" + (first.getLeader().getHits() - second.getLeader().getHits());
							opponentHits = String.valueOf(second.getLeader().getHits() - first.getLeader().getHits());
						}
						else if ((first.getLeader().getHits() - second.getLeader().getHits()) < 0) {
							yoursHits = String.valueOf(first.getLeader().getHits() - second.getLeader().getHits());
							opponentHits = "+" + (second.getLeader().getHits() - first.getLeader().getHits());
						}*/
						for (String s : config.getStringList("BOARD.SPECTATE.1V1.BOXING")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
						}
						return lines;
					}
					else if (kit.getGameRules().isBridge()) {
						for (String s : config.getStringList("BOARD.SPECTATE.1V1.BRIDGE.LINES")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, 1))
									.replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, 1))
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
						}
						return lines;
					}
					else if (kit.getGameRules().isHcfTrap()) {
						for (String s : config.getStringList("BOARD.SPECTATE.1V1.HCF_TRAP")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
						}
						return lines;
					}
					else if (kit.getGameRules().isHcf() && Profile.get(first.getLeader().getUuid()).getParty() != null) {
						for (String s : config.getStringList("BOARD.SPECTATE.1V1.HCF.LINES")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
						}
						return lines;
					}
					else if (kit.getGameRules().isParkour()) {
						// TODO: Variable para tomar el que mas checkpoints lleve
						for (String s : config.getStringList("BOARD.SPECTATE.1V1.PARKOUR")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
						}
						return lines;
					} else {
						for (String s : config.getStringList("BOARD.SPECTATE.1V1.DEFAULT")) {
							lines.add(s.replace("{duration}", getDuration())
									.replace("{arena-author}", getArena().getAuthor())
									.replace("{kit}", kit.getName())
									.replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
						}
						return lines;
					}
				}
			}
		}

		return lines;
	}

	@Override
	public void addSpectator(Player spectator, Player target, boolean follow) {
		super.addSpectator(spectator, target, follow);

		ChatColor firstColor;
		ChatColor secondColor;

		if (participantA.containsPlayer(target.getUniqueId())) {
			firstColor = ChatColor.GREEN;
			secondColor = ChatColor.RED;
		} else {
			firstColor = ChatColor.RED;
			secondColor = ChatColor.GREEN;
		}

		if (ranked) {
			new MessageFormat(Locale.MATCH_START_SPECTATING_RANKED.format(Profile.get(spectator.getUniqueId()).getLocale()))
				.add("{first_color}", firstColor.toString())
				.add("{participant_a}", participantA.getConjoinedNames())
				.add("{participant_a_elo}", String.valueOf(participantA.getLeader().getElo()))
				.add("{second_color}", secondColor.toString())
				.add("{participant_b}", participantB.getConjoinedNames())
				.add("{participant_b_elo}", String.valueOf(participantB.getLeader().getElo()))
				.send(spectator);
		} else {
			new MessageFormat(Locale.MATCH_START_SPECTATING.format(Profile.get(spectator.getUniqueId()).getLocale()))
				.add("{first_color}", firstColor.toString())
				.add("{participant_a}", participantA.getConjoinedNames())
				.add("{second_color}", secondColor.toString())
				.add("{participant_b}", participantB.getConjoinedNames())
 				.send(spectator);
		}
	}

	@Override
	public List<BaseComponent[]> generateEndComponents(Player player) {
		List<BaseComponent[]> componentsList = new ArrayList<>();
		Profile profile = Profile.get(player.getUniqueId());

		for (String line : Locale.MATCH_END_DETAILS.getStringList(profile.getLocale())) {
			if (line.equalsIgnoreCase("%INVENTORIES%")) {

				BaseComponent[] winners = generateInventoriesComponents(
					new MessageFormat(Locale.MATCH_END_WINNER_INVENTORY.format(profile.getLocale()))
						.add("{context}", participantA.getPlayers().size() == 1 ? "" : profile.getLocale() == Lang.ENGLISH ? "s" : "es")
						.toString(), winningParticipant);

				BaseComponent[] losers = generateInventoriesComponents(
					new MessageFormat(Locale.MATCH_END_LOSER_INVENTORY.format(profile.getLocale()))
						.add("{context}", participantB.getPlayers().size() > 1 ? profile.getLocale() == Lang.ENGLISH ? "s" : "es" : "").toString(), losingParticipant);

				if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1) {
					ChatComponentBuilder builder = new ChatComponentBuilder("");

					for (BaseComponent component : winners) {
						builder.append((TextComponent) component);
					}

					builder.append(new ChatComponentBuilder("&7 - ").create());

					for (BaseComponent component : losers) {
						builder.append((TextComponent) component);
					}

					componentsList.add(builder.create());
				} else {
					componentsList.add(winners);
					componentsList.add(losers);
				}

				continue;
			}

			if (line.equalsIgnoreCase("%ELO_CHANGES%")) {
				if (participantA.getPlayers().size() == 1 && participantB.getPlayers().size() == 1 && ranked) {
					List<String> sectionLines = new MessageFormat(Locale.MATCH_ELO_CHANGES.getStringList(profile.getLocale()))
						.add("{winning_name}", winningParticipant.getConjoinedNames())
						.add("{winning_elo_mod}", String.valueOf(winningParticipant.getLeader().getEloMod()))
						.add("{winning_elo_mod_elo}",
							String.valueOf((winningParticipant.getLeader().getElo() + winningParticipant.getLeader().getEloMod())))
						.add("{losser_name}", losingParticipant.getConjoinedNames())
						.add("{losser_elo_mod}", String.valueOf(losingParticipant.getLeader().getEloMod()))
						.add("{losser_elo_mod_elo}",
							String.valueOf((losingParticipant.getLeader().getElo() - winningParticipant.getLeader().getEloMod())))
						.toList();

					for (String sectionLine : sectionLines) {
						componentsList.add(new ChatComponentBuilder("").parse(sectionLine).create());
					}
				}

				continue;
			}

			line = line.replace("{spectators}", String.valueOf(this.getSpectators().size()));
			componentsList.add(new ChatComponentBuilder("").parse(line).create());
		}

		return componentsList;
	}

}
