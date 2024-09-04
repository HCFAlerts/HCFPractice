package me.hcfalerts.practice.match;

import com.google.common.collect.Lists;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.arena.impl.StandaloneArena;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.kit.KitLoadout;
import me.hcfalerts.practice.knockback.Knockback;
import me.hcfalerts.practice.match.events.MatchEndEvent;
import me.hcfalerts.practice.match.impl.BasicFreeForAllMatch;
import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.match.impl.BasicTeamRoundMatch;
import me.hcfalerts.practice.match.menu.ViewInventoryMenu;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.utilities.*;
import me.hcfalerts.practice.utilities.item.ItemUtil;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import me.hcfalerts.practice.utilities.string.TimeUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEnderPearl;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;

public class MatchListener implements Listener {

	private static final LinkedList<Material> interactuadles = new LinkedList<>(Arrays.asList(Material.ACACIA_DOOR,
			Material.ACACIA_FENCE_GATE, Material.ANVIL, Material.BEACON, Material.BED, Material.BIRCH_DOOR,
			Material.BIRCH_FENCE_GATE, Material.BOAT, Material.BREWING_STAND, Material.COMMAND, Material.CHEST,
			Material.DARK_OAK_DOOR, Material.DARK_OAK_FENCE_GATE, Material.DAYLIGHT_DETECTOR, Material.DAYLIGHT_DETECTOR_INVERTED,
			Material.DISPENSER, Material.DROPPER, Material.ENCHANTMENT_TABLE, Material.ENDER_CHEST, Material.FENCE_GATE,
			Material.FURNACE, Material.HOPPER, Material.HOPPER_MINECART, Material.ITEM_FRAME, Material.JUNGLE_DOOR,
			Material.JUNGLE_FENCE_GATE, Material.LEVER, Material.MINECART, Material.NOTE_BLOCK, Material.POWERED_MINECART,
			Material.REDSTONE_COMPARATOR, Material.REDSTONE_COMPARATOR_OFF, Material.REDSTONE_COMPARATOR_ON,
			Material.SIGN, Material.SIGN_POST, Material.STORAGE_MINECART, Material.TRAP_DOOR, Material.TRAPPED_CHEST,
			Material.WALL_SIGN, Material.WOOD_BUTTON, Material.WOOD_DOOR));

	private final LinkedList<ItemStack> interactEventAccept = Lists.newLinkedList();

	public MatchListener() {
 		for (String s : HCFPractice.get().getMainConfig().getStringList("MATCH.ITEMS_ALLOWED_INTERACT_PRE_START_ROUND")) {
			if (ItemUtil.getItemStack(s) == null) {
				System.out.println("[Practice] Could not deserialize item: " + s);
				continue;
			}
			interactEventAccept.add(ItemUtil.getItemStack(s));
		}
	}

	@EventHandler
	public void onPortal(PlayerPortalEvent event) {
		event.setCancelled(true);
		Player player = event.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());
		if (profile.getState() == ProfileState.FIGHTING) {
			if (profile.getMatch().getKit().getGameRules().isBridge()) {
				if (player.getLocation().getBlock().getType() == Material.ENDER_PORTAL ||
						player.getLocation().getBlock().getType() == Material.ENDER_PORTAL_FRAME) {
					if (LocationUtil.isTeamPortal(player)) return;
					BasicTeamRoundMatch match = (BasicTeamRoundMatch) profile.getMatch();
					if (match.getState() == MatchState.ENDING_ROUND || match.getState() == MatchState.ENDING_MATCH)
						return;
					for (GameParticipant<MatchGamePlayer> participant : match.getParticipants()) {
						for (MatchGamePlayer participantPlayer : participant.getPlayers()) {
							Player other = participantPlayer.getPlayer();

							new MessageFormat(Locale.MATCH_BRIDGE_SCORED
									.format(Profile.get(other.getUniqueId()).getLocale()))
									.add("{color}", match.getRelationColor(other, player).toString())
									.add("{player}", player.getName())
									.send(other);
						}
					}

					GameParticipant<MatchGamePlayer> otherTeam = match.getParticipantA()
							.containsPlayer(player.getUniqueId()) ?
							match.getParticipantB() :
							match.getParticipantA();

					for (MatchGamePlayer otherTeamPlayer : otherTeam.getPlayers()) {
						otherTeamPlayer.setDead(true);
					}
					otherTeam.setEliminated(true);

					if (match.canEndRound()) {
						match.setState(MatchState.ENDING_ROUND);
						match.getLogicTask().setNextAction(2);
						match.onRoundEnd();

						if (match.canEndMatch()) match.setState(MatchState.ENDING_MATCH);
					}
				}
			}
		}
	}

	@EventHandler
	public void onBlockFormEvent(BlockFormEvent event) {
		for (Match match : Match.getMatches()) {
			Arena arena = match.getArena();
			int x = (int) event.getBlock().getLocation().getX();
			int y = (int) event.getBlock().getLocation().getY();
			int z = (int) event.getBlock().getLocation().getZ();

			if (y > arena.getMaxBuildHeight()) {
				event.setCancelled(true);
				return;
			}

			if (arena.contains(x, y, z)) {
				match.getChangedBlocks().add(event.getBlock().getState());
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockFromEvent(BlockFromToEvent event) {
		for (Match match : Match.getMatches()) {
			Arena arena = match.getArena();
			int x = (int) event.getToBlock().getLocation().getX();
			int y = (int) event.getToBlock().getLocation().getY();
			int z = (int) event.getToBlock().getLocation().getZ();

			if (arena.contains(x, y, z) && match.getState() == MatchState.PLAYING_ROUND) {
				match.getPlacedBlocks().add(event.getBlock().getLocation());
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();
			if ((match.getKit().getGameRules().isBuild() || match.getKit().getGameRules().isSkywars()
					|| (match.getKit().getGameRules().isHcfTrap() && ((BasicTeamMatch) match).getParticipantA().containsPlayer(player.getUniqueId())))
					&& match.getState() == MatchState.PLAYING_ROUND || match.getState() == MatchState.STARTING_ROUND) {
				if (match.getKit().getGameRules().isSpleef()) {
					event.setCancelled(true);
					return;
				}

				Arena arena = match.getArena();
				int x = (int) event.getBlockPlaced().getLocation().getX();
				int y = (int) event.getBlockPlaced().getLocation().getY();
				int z = (int) event.getBlockPlaced().getLocation().getZ();

				if (y > arena.getMaxBuildHeight()) {
					new MessageFormat(Locale.ARENA_REACHED_MAXIMUM
							.format(profile.getLocale()))
							.send(player);
					event.setCancelled(true);
					return;
				}

				if (arena instanceof StandaloneArena) {
					StandaloneArena standaloneArena = (StandaloneArena) arena;
					if (standaloneArena.getSpawnBlue() != null && standaloneArena.getSpawnBlue().contains(event.getBlockPlaced())) {
						event.setCancelled(true);
						return;
					}
					if (standaloneArena.getSpawnRed() != null && standaloneArena.getSpawnRed().contains(event.getBlockPlaced())) {
						event.setCancelled(true);
						return;
					}
				}

				if (arena.contains(x, y, z)) {
					if (event.getBlockReplacedState() == null || event.getBlockReplacedState().getType() == Material.AIR) {
						match.getPlacedBlocks().add(event.getBlock().getLocation());
					} else {
						match.getChangedBlocks().add(event.getBlockReplacedState());
					}
				} else {
					new MessageFormat(Locale.ARENA_BUILD_OUTSIDE
							.format(profile.getLocale()))
							.send(player);
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		} else {
			if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE || profile.getState() == ProfileState.SPECTATING) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();

			if ((match.getKit().getGameRules().isBuild() || match.getKit().getGameRules().isSkywars()
					|| match.getKit().getGameRules().isSpleef() || (match.getKit().getGameRules().isHcfTrap()
					&& ((BasicTeamMatch) match).getParticipantA().containsPlayer(player.getUniqueId())))
					&& match.getState() == MatchState.PLAYING_ROUND) {
				if (match.getKit().getGameRules().isSpleef()) {
					if (event.getBlock().getType() == Material.SNOW_BLOCK ||
							event.getBlock().getType() == Material.SNOW) {
						match.getChangedBlocks().add(event.getBlock().getState());

						event.getBlock().setType(Material.AIR);
						player.getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
						player.updateInventory();
					} else {
						event.setCancelled(true);
					}
				} else if (match.getKit().getGameRules().isHcfTrap()) {
					if (match.getPlacedBlocks().contains(event.getBlock().getLocation())) {
						match.getPlacedBlocks().remove(event.getBlock().getLocation());
					} else {
						boolean b = true;
						for (BlockState state : match.getChangedBlocks()) {
							if (state.getLocation().equals(event.getBlock().getLocation())) {
								b = false;
								break;
							}
						}
						if (b) {
							match.getChangedBlocks().add(event.getBlock().getState());
						}
					}

//					event.getBlock().getDrops().forEach(item -> {
//						entities.add(event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), item));
//					});
					if (!event.getBlock().getDrops().isEmpty()) {
						ItemStack found = null;
						for (ItemStack itemStack : event.getBlock().getDrops()) {
							found = itemStack;
							break;
						}
						match.getDroppedItems().add(event.getBlock().getLocation().getWorld().dropItemNaturally(event.getBlock().getLocation(), found));
					}

					event.getBlock().setType(Material.AIR);
					event.setCancelled(true);
				} else if (!match.getPlacedBlocks().remove(event.getBlock().getLocation())) {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		} else {
			if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE || profile.getState() == ProfileState.SPECTATING) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onBucketEmptyEvent(PlayerBucketEmptyEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();

			if ((match.getKit().getGameRules().isBuild() || match.getKit().getGameRules().isSkywars()
					|| (match.getKit().getGameRules().isHcfTrap() && ((BasicTeamMatch) match).getParticipantA().containsPlayer(player.getUniqueId())))
					&& match.getState() == MatchState.PLAYING_ROUND) {
				Arena arena = match.getArena();
				Block block = event.getBlockClicked().getRelative(event.getBlockFace());
				int x = (int) block.getLocation().getX();
				int y = (int) block.getLocation().getY();
				int z = (int) block.getLocation().getZ();

				if (y > arena.getMaxBuildHeight()) {
					new MessageFormat(Locale.ARENA_REACHED_MAXIMUM
							.format(profile.getLocale()))
							.send(player);
					event.setCancelled(true);
					return;
				}

				if (x >= arena.getX1() && x <= arena.getX2() && y >= arena.getY1() && y <= arena.getY2() &&
						z >= arena.getZ1() && z <= arena.getZ2()) {
					match.getPlacedBlocks().add(block.getLocation());
				} else {
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		} else {
			if (!player.isOp() || player.getGameMode() != GameMode.CREATIVE || profile.getState() == ProfileState.SPECTATING) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerPickupItemEvent(PlayerPickupItemEvent event) {
		Profile profile = Profile.get(event.getPlayer().getUniqueId());

		if (profile.getState() == ProfileState.SPECTATING) {
			event.setCancelled(true);
			return;
		}

		if (profile.getState() == ProfileState.FIGHTING) {
			if (profile.getMatch().getGamePlayer(event.getPlayer()).isDead()) {
				event.setCancelled(true);
				return;
			}

			if (event.getItem().getItemStack().getType().name().contains("BOOK")) {
				event.setCancelled(true);
				return;
			}

			Iterator<Item> itemIterator = profile.getMatch().getDroppedItems().iterator();

			while (itemIterator.hasNext()) {
				Item item = itemIterator.next();

				if (item.equals(event.getItem())) {
					itemIterator.remove();
					return;
				}
			}

			if (profile.getMatch().getKit().getGameRules().isHcfTrap()) return;

			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) {
		Profile profile = Profile.get(event.getPlayer().getUniqueId());

		if (event.getItemDrop().getItemStack().getType() == Material.BOOK ||
				event.getItemDrop().getItemStack().getType() == Material.ENCHANTED_BOOK) {
			event.setCancelled(true);
			return;
		}

		if (profile.getState() == ProfileState.FIGHTING) {
			if (HCFPractice.get().getMainConfig().getBoolean("MATCH.REMOVE_GLASS_BOTTLE_ON_DROP") &&
					event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE) {
				event.getItemDrop().remove();
				return;
			}

			if (profile.getMatch().getKit().getGameRules().isBridge()) {
				event.setCancelled(true);
				return;
			}

			if (event.getItemDrop().getItemStack().getType().name().endsWith("_SWORD")) {
				event.setCancelled(true);
				return;
			}

			profile.getMatch().getDroppedItems().add(event.getItemDrop());
		} else if (profile.getState() == ProfileState.SPECTATING) event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) {
		event.getEntity().spigot().respawn();
		event.setDeathMessage(null);

		Profile profile = Profile.get(event.getEntity().getUniqueId());

		if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();
			if (match.getKit().getGameRules().isBridge()) {
				Player killer = PlayerUtil.getLastAttacker(event.getEntity());
				match.sendDeathMessage(event.getEntity(), killer);
			}

			if (match.getKit().getGameRules().isBridge()) event.getDrops().clear();

			if (HCFPractice.get().getMainConfig().getBoolean("MATCH.DROP_ITEMS_ON_DEATH")) {
				List<Item> entities = Lists.newArrayList();
				event.getDrops().forEach(itemStack -> {
					if (!(itemStack.getType() == Material.BOOK || itemStack.getType() == Material.ENCHANTED_BOOK)) {
						entities.add(event.getEntity().getLocation().getWorld().dropItemNaturally(event.getEntity().getLocation(), itemStack));
					}
				});
				event.getDrops().clear();

				profile.getMatch().getDroppedItems().addAll(entities);
			} else event.getDrops().clear();

			profile.getMatch().onDeath(event.getEntity(), false);
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		event.setRespawnLocation(event.getPlayer().getLocation());
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileLaunchEvent(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player) {
			Player shooter = (Player) event.getEntity().getShooter();
			Profile profile = Profile.get(shooter.getUniqueId());

			if (profile.getState() == ProfileState.FIGHTING) {
				Match match = profile.getMatch();

				if (match.getState() == MatchState.STARTING_ROUND) {
					event.setCancelled(true);
				} else if (match.getState() == MatchState.PLAYING_ROUND) {

					if (match.getKit().getGameRules().isBridge() && event.getEntity() instanceof Arrow) {
						TaskUtil.runLater(() -> {
							if (match.getState() == MatchState.PLAYING_ROUND) {
								shooter.getInventory().addItem(new ItemStack(Material.ARROW, 1));
							}
						}, 20L * HCFPractice.get().getMainConfig().getInteger("MATCH.BRIDGE_BOW_COOLDOWN"));
						return;
					}

					if (event.getEntity() instanceof ThrownPotion) {
						if (HCFPractice.get().getMainConfig().getBoolean("MATCH.FAST_POTION")) {
							Projectile projectile = event.getEntity();

							if (shooter.isSprinting()) {
								Vector velocity = projectile.getVelocity();

								velocity.setY(-1.1);
								projectile.setVelocity(velocity);
							}
						}
						match.getGamePlayer(shooter).incrementPotionsThrown();
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onProjectileHitEvent(ProjectileHitEvent event) {
		if (event.getEntity() instanceof Arrow) {
			if (event.getEntity().getShooter() instanceof Player) {
				Player shooter = (Player) event.getEntity().getShooter();
				Profile shooterData = Profile.get(shooter.getUniqueId());

				if (shooterData.getState() == ProfileState.FIGHTING) {
					shooterData.getMatch().getGamePlayer(shooter).handleHit();
				}
			}

			if (event.getEntity().isOnGround()) event.getEntity().remove();
		}
	}

	@EventHandler
	public void onPearlLand(PlayerTeleportEvent event) {
		Location to = event.getTo();

		if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			Profile profile = Profile.get(event.getPlayer().getUniqueId());
			if (profile.getState() != ProfileState.FIGHTING && profile.getState() != ProfileState.EVENT) {
				event.setCancelled(true);
				return;
			}
			to.setX(to.getBlockX() + 0.5);
			to.setZ(to.getBlockZ() + 0.5);
			event.setTo(to);
			Location pearlLocation = event.getTo();
			Location playerLocation = event.getFrom();

			if (playerLocation.getBlockY() < pearlLocation.getBlockY()) {
				Block block = pearlLocation.getBlock();

				for (BlockFace face : BlockFace.values()) {
					Material type = block.getRelative(face).getType();

					if (type == Material.GLASS || type == Material.BARRIER) {
						pearlLocation.setY(pearlLocation.getBlockY() - 1.0);
						break;
					}
				}
			} else pearlLocation.setY(pearlLocation.getBlockY() + 0.2);

			event.setTo(pearlLocation);
		}
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPotionSplashEvent(PotionSplashEvent event) {
		if (event.getPotion().getShooter() instanceof Player) {
			Player shooter = (Player) event.getPotion().getShooter();
			Profile shooterData = Profile.get(shooter.getUniqueId());

			if (shooterData.getState() == ProfileState.FIGHTING &&
					shooterData.getMatch().getState() == MatchState.PLAYING_ROUND) {

				if (event.getIntensity(shooter) <= 0.5D) {
					shooterData.getMatch().getGamePlayer(shooter).incrementPotionsMissed();
				}
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		Profile profile = Profile.get(player.getUniqueId());
		if (profile.getState() == ProfileState.SPECTATING) event.setCancelled(true);
	}

	@EventHandler
	public void onInventoryClick(InventoryInteractEvent event) {
		Player player = (Player) event.getWhoClicked();
		Profile profile = Profile.get(player.getUniqueId());
		if (profile.getState() == ProfileState.SPECTATING) event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player healed = (Player) event.getEntity();
			Profile healedProfile = Profile.get(healed.getUniqueId());

			if (event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
				if (healedProfile.getState() == ProfileState.FIGHTING && !healedProfile.getMatch().getKit().getGameRules().isHealthRegeneration()) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getState() == ProfileState.FIGHTING) {
				Match match = profile.getMatch();
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
					if (match.getKit().getGameRules().isBridge()) {
						Player killer = PlayerUtil.getLastAttacker(player);
						match.sendDeathMessage(player, killer);
					}
					else if (match.getKit().getGameRules().isParkour()) {
						player.teleport(match.getGamePlayer(player).getCheckPoint());
					}

					if (match.getState() == MatchState.ENDING_MATCH) {
						event.setCancelled(true);
						return;
					}
					profile.getMatch().onDeath(player, false);
					return;
				}

				if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
					if (match.getKit().getGameRules().isNoFallDamage() || match.getKit().getGameRules().isBridge() || match.getKit().getGameRules().isParkour())
						event.setCancelled(true);
				}

				if (profile.getMatch().getState() != MatchState.PLAYING_ROUND) {
					event.setCancelled(true);
					return;
				}

				if (profile.getMatch().getGamePlayer(player).isDead()) {
					event.setCancelled(true);
					return;
				}

				if (profile.getMatch().getKit().getGameRules().isSumo() || profile.getMatch().getKit().getGameRules().isSpleef()
						|| profile.getMatch().getKit().getGameRules().isBoxing()) {
					event.setDamage(0);
					player.setHealth(20.0);
					player.updateInventory();
				}
			}

		}
	}

	@EventHandler
	public void onEntityDamageByEntityLow(EntityDamageByEntityEvent event) {
		Player attacker;

		if (event.getDamager() instanceof Player) {
			attacker = (Player) event.getDamager();
		} else if (event.getDamager() instanceof Projectile) {
			if (((Projectile) event.getDamager()).getShooter() instanceof Player) {
				attacker = (Player) ((Projectile) event.getDamager()).getShooter();
			} else {
				event.setCancelled(true);
				return;
			}
		} else {
			event.setCancelled(true);
			return;
		}

		if (attacker != null && event.getEntity() instanceof Player) {
			Player damaged = (Player) event.getEntity();
			Profile damagedProfile = Profile.get(damaged.getUniqueId());
			Profile attackerProfile = Profile.get(attacker.getUniqueId());

			if (attackerProfile.getState() == ProfileState.SPECTATING || damagedProfile.getState() == ProfileState.SPECTATING) {
				event.setCancelled(true);
				return;
			}

			if (event.getDamager() instanceof CraftEnderPearl) {
				event.setCancelled(false);
				return;
			}

			if (damagedProfile.getState() == ProfileState.FIGHTING && attackerProfile.getState() == ProfileState.FIGHTING) {
				Match match = attackerProfile.getMatch();

				if (!damagedProfile.getMatch().getMatchId().equals(attackerProfile.getMatch().getMatchId())) {
					event.setCancelled(true);
					return;
				}

				if (match.getGamePlayer(damaged).isDead()) {
					event.setCancelled(true);
					return;
				}

				if (match.getGamePlayer(attacker).isDead()) {
					event.setCancelled(true);
					return;
				}

				if (match.isOnSameTeam(damaged, attacker)) {
					event.setCancelled(true);
					return;
				}

				if (match.getKit().getGameRules().isParkour()) {
					event.setCancelled(true);
					return;
				}

				attackerProfile.getMatch().getGamePlayer(attacker).handleHit();
				damagedProfile.getMatch().getGamePlayer(damaged).resetCombo();

				if (match.getKit().getGameRules().isBoxing()) {
					if (match.getGamePlayer(attacker).getHits() == HCFPractice.get().getMainConfig().getInteger("MATCH.BOXING_MAX_HITS")) {
						match.onDeath(damaged, false);
					}
				}

				if (event.getDamager() instanceof Arrow) {
					int range = (int) Math.ceil(event.getEntity().getLocation().distance(attacker.getLocation()));
					double health = Math.ceil(damaged.getHealth() - event.getFinalDamage()) / 2.0D;

					new MessageFormat(Locale.ARROW_DAMAGE_INDICATOR.format(attackerProfile.getLocale()))
							.add("{range}", String.valueOf(range))
							.add("{damaged_name}", damaged.getName())
							.add("{damaged_health}", String.valueOf(health))
							.add("{symbol}", StringEscapeUtils.unescapeJava("\u2764"))
							.send(attacker);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEntityDamageByEntityMonitor(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player victim = (Player) event.getEntity();
			Player attacker = null;

			if (event.getDamager() instanceof Player) {
				attacker = (Player) event.getDamager();
			} else if (event.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) event.getDamager();

				if (projectile.getShooter() instanceof Player) {
					attacker = (Player) projectile.getShooter();
				}
			}

			if (attacker != null) {
				PlayerUtil.setLastAttacker(victim, attacker);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerItemConsumeEvent(PlayerItemConsumeEvent event) {
		Player player = event.getPlayer();
		if (event.getItem().getType() == Material.GOLDEN_APPLE) {
			if (event.getItem().hasItemMeta() &&
					event.getItem().getItemMeta().getDisplayName().contains("Golden Head")) {
				if (HCFPractice.get().getMainConfig().getBoolean("MATCH.INSTANT_GOLDEN_REGEN")) {
					double healthAdd = HCFPractice.get().getMainConfig().getDouble("MATCH.GOLDEN_HEAD_HEALTH_REGEN");
					if (player.getHealth() + healthAdd >= player.getMaxHealth()) {
						player.setHealth(player.getMaxHealth());
					} else if (player.getHealth() + healthAdd < player.getMaxHealth()) {
						player.setHealth(player.getHealth() + healthAdd);
					}
				} else {
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
				}
				player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
				player.setFoodLevel(Math.min(player.getFoodLevel() + 6, 20));
			}
		} else if (HCFPractice.get().getMainConfig().getBoolean("MATCH.REMOVE_GLASS_BOTTLE_ON_CONSUME")) {
			if (event.getItem().getTypeId() == 373) {
				TaskUtil.runLaterAsync(() -> {
					player.setItemInHand(new ItemStack(Material.AIR));
					player.updateInventory();
				}, 1L);
			}
		}
	}

	@EventHandler
	public void onFoodLevelChange(FoodLevelChangeEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getState() == ProfileState.FIGHTING && profile.getMatch().getState() == MatchState.PLAYING_ROUND &&
					!profile.getMatch().getKit().getGameRules().isNoFood()) {
				if (event.getFoodLevel() >= 20) {
					event.setFoodLevel(20);
					player.setSaturation(20);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Profile profile = Profile.get(event.getPlayer().getUniqueId());

		if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();

			if (match.getState() == MatchState.STARTING_ROUND || match.getState() == MatchState.PLAYING_ROUND
					|| (match.getState() == MatchState.ENDING_ROUND && match instanceof BasicTeamRoundMatch)) {
				profile.getMatch().onDisconnect(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerKickEvent(PlayerKickEvent event) {
		Profile profile = Profile.get(event.getPlayer().getUniqueId());

		if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();

			if (match.getState() == MatchState.STARTING_ROUND || match.getState() == MatchState.PLAYING_ROUND) {
				profile.getMatch().onDisconnect(event.getPlayer());
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getState() == ProfileState.SPECTATING && event.getRightClicked() instanceof Player &&
				player.getItemInHand() != null) {
			Player target = (Player) event.getRightClicked();

			if (Hotbar.fromItemStack(player.getItemInHand()) == HotbarItem.VIEW_INVENTORY) {
				new ViewInventoryMenu(target).openMenu(player);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractHCFTrap(PlayerInteractEvent event) {
		Player player = event.getPlayer();

		if (Profile.get(player.getUniqueId()).getState() == ProfileState.SPECTATING) {
			event.setCancelled(true);
			return;
		}

		if (Profile.get(player.getUniqueId()).getState() == ProfileState.FIGHTING) {
			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getMatch() != null) {
				Match match = profile.getMatch();

				if (event.getAction() == Action.RIGHT_CLICK_BLOCK && interactuadles.contains(event.getClickedBlock().getType())) {
					if (match.getKit().getGameRules().isHcfTrap()) {
						if (((BasicTeamMatch) match).getParticipantA().containsPlayer(player.getUniqueId())) {
							boolean b = true;
							for (BlockState changedBlock : match.getChangedBlocks()) {
								if (changedBlock.getBlock().getLocation().equals(event.getClickedBlock().getLocation())) {
									b = false;
									break;
								}
							}
							if (b) {
								match.getChangedBlocks().add(event.getClickedBlock().getState());
							}
						} else {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItem();

		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getState() == ProfileState.FIGHTING) {
				Match match = profile.getMatch();

				if (itemStack != null) {
					if (Hotbar.fromItemStack(itemStack) == HotbarItem.SPECTATE_STOP) {
						match.onDisconnect(player);
						return;
					}

					if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()) {
						ItemStack kitItem = Hotbar.getItems().get(HotbarItem.KIT_SELECTION).getItemStack();

						if (itemStack.getType() == kitItem.getType() &&
								itemStack.getDurability() == kitItem.getDurability()) {
							Matcher matcher = HotbarItem.KIT_SELECTION.getPattern().
									matcher(itemStack.getItemMeta().getDisplayName());

							if (matcher.find()) {
								String kitName = matcher.group(2);
								KitLoadout kitLoadout = null;

								if (kitName.equals("Default")) {
									kitLoadout = match.getKit().getKitLoadout();
								} else {
									for (KitLoadout find : profile.getKitData().get(match.getKit()).getLoadouts()) {
										if (find != null && find.getCustomName().equals(kitName)) {
											kitLoadout = find;
										}
									}
								}

								if (kitLoadout != null) {
									new MessageFormat(Locale.MATCH_GIVE_KIT.format(profile.getLocale()))
											.add("{kit_name}", kitLoadout.getCustomName())
											.send(player);
									if (match.getKit().getGameRules().isBridge()) {
										player.getInventory().setContents(kitLoadout.getContents());
										KitUtils.giveBridgeKit(player);
										profile.setSelectedKit(kitLoadout);
									} else {
										player.getInventory().setArmorContents(kitLoadout.getArmor());
										player.getInventory().setContents(kitLoadout.getContents());
									}
									player.updateInventory();
									event.setCancelled(true);
								}
							}
						}
					}

					if (match.getState() == MatchState.STARTING_ROUND) {
						for (ItemStack stack : interactEventAccept) {
							if (itemStack.isSimilar(stack)) {
								event.setCancelled(false);
								return;
							}
						}
						
						if (itemStack.getType() == Material.POTION && Potion.fromItemStack(itemStack) != null) {
							Potion potion = Potion.fromItemStack(itemStack);
							if (!potion.isSplash()) {
								event.setCancelled(false);
							}
						} else {
							event.setCancelled(true);
							player.updateInventory();
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onThrowEnderpearl(ProjectileLaunchEvent event) {
		if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
			Player shooter = (Player) event.getEntity().getShooter();
			Profile profile = Profile.get(shooter.getUniqueId());
			if (profile.getMatch() != null) {
				Match match = profile.getMatch();

				// Deny pearl if match hasn't started
				if (match.getState() != MatchState.PLAYING_ROUND) {
					shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
					event.setCancelled(true);
					return;
				}

				if (!profile.getEnderpearlCooldown().hasExpired()) {
					String time = TimeUtil.millisToSeconds(profile.getEnderpearlCooldown().getRemaining());
					new MessageFormat(Locale.MATCH_ENDERPEARL_COOLDOWN.format(profile.getLocale()))
							.add("{context}", (time.equalsIgnoreCase("1.0") ? "" : "s"))
							.add("{time}", time)
							.send(shooter);
					shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
					event.setCancelled(true);
				} else {
					profile.setEnderpearlCooldown(new Cooldown(16_000));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onMove(PlayerMoveEvent event) {
//		if (event.getFrom().getBlockX() == event.getTo().getBlockX() &&
//				event.getFrom().getBlockZ() == event.getTo().getBlockZ() &&
//				event.getFrom().getBlockY() == event.getTo().getBlockY()) {
//			return;
//		}

		if (Profile.get(event.getPlayer().getUniqueId()).getState() == ProfileState.FIGHTING) {
			Player player = event.getPlayer();
			Match match = Profile.get(player.getUniqueId()).getMatch();
			if (match.getKit().getGameRules().isSumo() || match.getKit().getGameRules().isSpleef()) {
				if (player.getLocation().getBlock().getType() == Material.WATER
						|| player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
					TaskUtil.run(() -> match.onDeath(player, false));
				}
			}
		}
	}

	@EventHandler
	public void onParkourRuleMove(PlayerMoveEvent event) {
		if (Profile.get(event.getPlayer().getUniqueId()).getState() == ProfileState.FIGHTING) {
			Player player = event.getPlayer();
			Match match = Profile.get(player.getUniqueId()).getMatch();
			MatchGamePlayer gamePlayer = match.getGamePlayer(player);
			if (match.getKit().getGameRules().isParkour() && match.getState() == MatchState.PLAYING_ROUND) {
				if (player.getLocation().getBlock().getType().name().endsWith("_PLATE")) {
					if (event.getTo().getBlockX() == event.getFrom().getBlockX() &&
							event.getTo().getBlockZ() == event.getFrom().getBlockZ() &&
							event.getTo().getBlockY() == event.getFrom().getBlockY()) {
						return;
					}

					Material upperBlock = player.getLocation().getBlock().getRelative(BlockFace.UP).getType();
					if (upperBlock == Material.STONE_PLATE || player.getLocation().getBlock().getType() == Material.STONE_PLATE
							&& !gamePlayer.isSameCheckpoint(player.getLocation())) {
						gamePlayer.incrementCheckpoints();
						gamePlayer.setCheckPoint(player.getLocation());
						new MessageFormat(Locale.MATCH_SET_CHECKPOINT.format(Profile.get(player.getUniqueId()).getLocale()))
								.add("{checkpoints}", String.valueOf(gamePlayer.getCheckPoints()))
								.send(player);
					}
					else if (upperBlock == Material.GOLD_PLATE || player.getLocation().getBlock().getType() == Material.GOLD_PLATE) {
						if (match instanceof BasicTeamMatch) {
							BasicTeamMatch teamMatch = (BasicTeamMatch) match;
							Player deadPlayer = teamMatch.getParticipantA().containsPlayer(player.getUniqueId()) ?
									teamMatch.getParticipantB().getLeader().getPlayer() :
									teamMatch.getParticipantA().getLeader().getPlayer();
							TaskUtil.run(() -> match.onDeath(deadPlayer, false));
						}
						else if (match instanceof BasicFreeForAllMatch) {
							BasicFreeForAllMatch freeForAllMatch = (BasicFreeForAllMatch) match;
							for (GameParticipant<MatchGamePlayer> participant : freeForAllMatch.getParticipants()) {
								for (MatchGamePlayer participantPlayer : participant.getPlayers()) {
									if (participantPlayer.equals(gamePlayer)) continue;
									TaskUtil.run(() -> match.onDeath(participantPlayer.getPlayer(), false));
								}
							}
						}
					}
				}
				else if (player.getLocation().getBlock().getType() == Material.WATER
						|| player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
					if (gamePlayer.getCheckPoint() == null) {
						player.teleport(match.getArena().getSpawnA());
					} else {
						player.teleport(match.getGamePlayer(player).getCheckPoint());
					}
				}
			}
		}
	}

	@EventHandler
	public void onParkourItemsInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

		Player player = event.getPlayer();
		if (Profile.get(player.getUniqueId()).getState() == ProfileState.FIGHTING) {
			Profile profile = Profile.get(player.getUniqueId());
			Match match = Profile.get(player.getUniqueId()).getMatch();
			MatchGamePlayer gamePlayer = match.getGamePlayer(player);
			if (match.getKit().getGameRules().isParkour()) {
				event.setCancelled(true);
				if (event.getItem() != null) {
					if (event.getItem().isSimilar(Hotbar.getItem(HotbarItem.PARKOUR_HIDE_PLAYERS))) {
						if (gamePlayer.isHidePlayers()) {
							gamePlayer.setHidePlayers(false);
							new MessageFormat(Locale.MATCH_PARKOUR_SHOW_PLAYERS.format(profile.getLocale()))
									.send(player);
							for (GameParticipant<MatchGamePlayer> participant : match.getParticipants()) {
								for (MatchGamePlayer participantPlayer : participant.getPlayers()) {
									if (participantPlayer.equals(gamePlayer)) continue;
									player.showPlayer(participantPlayer.getPlayer());
								}
							}
						} else {
							gamePlayer.setHidePlayers(true);
							new MessageFormat(Locale.MATCH_PARKOUR_HIDE_PLAYERS.format(profile.getLocale()))
									.send(player);
							for (GameParticipant<MatchGamePlayer> participant : match.getParticipants()) {
								for (MatchGamePlayer participantPlayer : participant.getPlayers()) {
									if (participantPlayer.equals(gamePlayer)) continue;
									player.hidePlayer(participantPlayer.getPlayer());
								}
							}
						}
					}
					else if (event.getItem().isSimilar(Hotbar.getItem(HotbarItem.PARKOUR_LAST_CHECKPOINT))) {
						if (gamePlayer.getCheckPoint() == null) {
							player.teleport(match.getArena().getSpawnA());
						} else {
							player.teleport(gamePlayer.getCheckPoint());
						}

						new MessageFormat(Locale.MATCH_PARKOUR_LAST_CHECKPOINT.format(profile.getLocale()))
								.send(player);
					}
					else if (event.getItem().isSimilar(Hotbar.getItem(HotbarItem.PARKOUR_RESET))) {
						player.teleport(match.getArena().getSpawnA());

						new MessageFormat(Locale.MATCH_PARKOUR_RESET.format(profile.getLocale()))
								.send(player);
					}
				}
			}
		}
	}

	@EventHandler
	public void onMathEnd(MatchEndEvent event) {
		event.getMatch().getParticipants().forEach(matchGamePlayerGameParticipant -> {
			matchGamePlayerGameParticipant.getPlayers().forEach(player -> {
				if (player.getPlayer() == null) return;
				Knockback.getKnockbackProfiler().setKnockback(player.getPlayer(), "default");
			});
		});
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandPreProcess(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());
		if (profile.getState() == ProfileState.FIGHTING || profile.getState() == ProfileState.SPECTATING) {
			if (HCFPractice.get().getMainConfig().getStringList("MATCH.DISABLED_COMMANDS").contains(event.getMessage())) {
				if (HCFPractice.get().getMainConfig().getBoolean("MATCH.CAN_OP_USE_DISABLED_COMMANDS") && player.isOp())
					return;
				new MessageFormat(Locale.DUEL_DISABLED_COMMAND.format(Profile.get(player.getUniqueId()).getLocale()))
						.send(player);
				event.setCancelled(true);
			}
		}
	}

	/*@EventHandler
	public void onElevatorInteractEvent(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());
		*//*if (profile.getState() == ProfileState.FIGHTING) {
			Match match = profile.getMatch();
			if (match.getKit().getGameRules().isHcf() || match.getKit().getGameRules().isHcfTrap()) {
				BasicTeamMatch basicTeamMatch = (BasicTeamMatch) match;
				if (HCFPractice.get().getMainConfig().getBoolean("MATCH.ONLY_FAC_OWNER_INTERACT_WITH_ELEVATOR")) {
					if (basicTeamMatch.getParticipantA().containsPlayer(player.getUniqueId())) {*//*
						if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.WALL_SIGN
								|| event.getClickedBlock().getType() == Material.SIGN_POST)) {
							Sign sign = (Sign) event.getClickedBlock().getState();
							if (sign.getLines()[0].equalsIgnoreCase("[elevator]")) {
								if (sign.getLines()[1].equalsIgnoreCase("up")) {

								}
								else if (sign.getLines()[1].equalsIgnoreCase("down")) {

								}
							}
						}
*//*					}
				} else {
					// TODO
				}
			}
		}*//*
	}*/
}
