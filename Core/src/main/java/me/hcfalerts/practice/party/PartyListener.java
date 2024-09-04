package me.hcfalerts.practice.party;

import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.party.menu.HCFClassMenu;
import me.hcfalerts.practice.party.menu.OtherPartiesMenu;
import me.hcfalerts.practice.party.menu.PartyEventSelectEventMenu;
import me.hcfalerts.practice.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyListener implements Listener {

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		Profile profile = Profile.get(event.getPlayer().getUniqueId());

		if (event.getMessage().startsWith("@") || event.getMessage().startsWith("!")) {
			if (profile.getParty() != null) {
				event.setCancelled(true);
				profile.getParty().sendChat(event.getPlayer(), ChatColor.stripColor(event.getMessage().substring(1)));
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR ||
		                                event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

			if (hotbarItem != null) {

				if (hotbarItem == HotbarItem.PARTY_EVENTS) {
					new PartyEventSelectEventMenu().openMenu(event.getPlayer());
					event.setCancelled(true);
				}
				else if (hotbarItem == HotbarItem.OTHER_PARTIES) {
					new OtherPartiesMenu().openMenu(event.getPlayer());
					event.setCancelled(true);
				}
				else if (hotbarItem == HotbarItem.CLASS_SELECT) {
					new HCFClassMenu().openMenu(event.getPlayer());
					event.setCancelled(true);
				}

			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Profile profile = Profile.get(event.getPlayer().getUniqueId());

		if (profile.getParty() != null) {
			if (profile.getParty().getLeader().equals(event.getPlayer())) {
				profile.getParty().disband();
			} else {
				profile.getParty().leave(event.getPlayer(), false);
			}
		}
	}

}
