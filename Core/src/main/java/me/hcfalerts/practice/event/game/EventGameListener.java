package me.hcfalerts.practice.event.game;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.event.impl.gulag.GulagGameLogic;
import me.hcfalerts.practice.event.impl.spleef.SpleefEvent;
import me.hcfalerts.practice.event.impl.tntrun.TNTRunEvent;
import me.hcfalerts.practice.event.impl.tntrun.TNTRunGameLogic;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.regex.Matcher;

public class EventGameListener implements Listener {

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		if (EventGame.getActiveGame() != null) {
			Profile profile = Profile.get(event.getPlayer().getUniqueId());

			if (profile.getState() == ProfileState.EVENT) {
				EventGame.getActiveGame().getGamePlayer(event.getPlayer()).setDisconnected(true);
				EventGame.getActiveGame().getGameLogic().onLeave(event.getPlayer());
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerMoveEvent(PlayerMoveEvent event) {
		if (EventGame.getActiveGame() != null) {
			Profile profile = Profile.get(event.getPlayer().getUniqueId());

			if (profile.getState() == ProfileState.EVENT) {
				if (EventGame.getActiveGame().getGameState() == EventGameState.PLAYING_ROUND) {
					EventGame.getActiveGame().getGameLogic().onMove(event.getPlayer());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onTnTRunTeleport(PlayerTeleportEvent event) {
		if (event.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN) {
			if (EventGame.getActiveGame() != null) {
				EventGame game = EventGame.getActiveGame();
				if (game.getGameLogic() instanceof TNTRunGameLogic) {
					TNTRunEvent tntRunEvent = (TNTRunEvent) game.getEvent();
					if (game.getGameState() == EventGameState.PLAYING_ROUND) {
						EventGame.getActiveGame().getGameLogic().onMove(event.getPlayer());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (EventGame.getActiveGame() != null) {
			Profile profile = Profile.get(event.getPlayer().getUniqueId());

			if (profile.getState() == ProfileState.EVENT) {
				if (EventGame.getActiveGame().getGameState() == EventGameState.PLAYING_ROUND) {
					EventGame.getActiveGame().getGameLogic().onInteract(event, event.getPlayer(), event.getItem());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
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
				Profile victimProfile = Profile.get(victim.getUniqueId());

				if (victimProfile.getState() == ProfileState.EVENT) {
					Profile attackerProfile = Profile.get(attacker.getUniqueId());

					if (attackerProfile.getState() == ProfileState.EVENT) {
						if (!EventGame.getActiveGame().getGameLogic().isPlaying(victim) ||
						    !EventGame.getActiveGame().getGameLogic().isPlaying(attacker) ||
						    EventGame.getActiveGame().getGameState() != EventGameState.PLAYING_ROUND) {
							event.setCancelled(true);
						} else {
							EventGame.getActiveGame().getGameLogic().onEntityDamageByPlayer(event, attacker, victim);
						}
					} else event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageEvent(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getState() == ProfileState.EVENT) {
				if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
					if (EventGame.getActiveGame().getGameState() == EventGameState.PLAYING_ROUND &&
					    EventGame.getActiveGame().getGameLogic().isPlaying(player)) {
						EventGame.getActiveGame().getGameLogic().onDeath(player, null);
					}
				}
				EventGame.getActiveGame().getGameLogic().onEntityDamage(event, player);
			}
		}
	}

	@EventHandler
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (EventGame.getActiveGame() != null) {
			Profile profile = Profile.get(event.getPlayer().getUniqueId());
			if (profile.getState() == ProfileState.EVENT) {
				if (EventGame.getActiveGame().getEvent() instanceof SpleefEvent) {
					SpleefEvent spleefEvent = (SpleefEvent) EventGame.getActiveGame().getEvent();
					if (HCFPractice.get().getMainConfig().getBoolean("EVENT.GIVE_SNOWBALL_SPLEEF")) {
						if (event.getBlock().getType() == Material.SNOW_BLOCK ||
								event.getBlock().getType() == Material.SNOW) {
							spleefEvent.getChangedBlocks().add(event.getBlock().getState());
							event.getBlock().setType(Material.AIR);
							event.getPlayer().getInventory().addItem(new ItemStack(Material.SNOW_BALL, 4));
							event.getPlayer().updateInventory();
						}
					}
					else {
						event.setCancelled(true);
					}
				}
			}
		}
	}


	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteractEventNormal(PlayerInteractEvent event) {
		if (EventGame.getActiveGame() != null) {
			if (event.getItem() != null &&
					(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
					event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
				Profile profile = Profile.get(event.getPlayer().getUniqueId());

				if (profile.getState() == ProfileState.EVENT) {
					ItemStack itemStack = event.getItem();
					ItemStack voteItem = Hotbar.getItems().get(HotbarItem.MAP_SELECTION).getItemStack();

					if (itemStack.getType() == voteItem.getType() &&
							itemStack.getDurability() == voteItem.getDurability()) {
						Matcher matcher = HotbarItem.MAP_SELECTION
								.getPattern().matcher(itemStack.getItemMeta().getDisplayName());

						if (matcher.find()) {
							String mapName = matcher.group(2);

							event.setCancelled(true);
							event.getPlayer().chat("/eventvote " + mapName);
						}
					}
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDeath(PlayerDeathEvent event) {
		if (EventGame.getActiveGame() != null) {
			Profile profile = Profile.get(event.getEntity().getUniqueId());
			if (profile.getState() == ProfileState.EVENT) {
				if (EventGame.getActiveGame().getGameLogic() instanceof GulagGameLogic) {
					EventGame.getActiveGame().getGameLogic().onDeath(event.getEntity(), null);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryClick(InventoryClickEvent event) {
		if (EventGame.getActiveGame() != null) {
			Player player = (Player) event.getWhoClicked();
			if (Profile.get(player.getUniqueId()).getState() == ProfileState.EVENT) {

				EventGame.getActiveGame().getGameLogic().onInventoryClick(event, player);
			}
		}
	}

}
