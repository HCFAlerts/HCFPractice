package me.hcfalerts.practice.queue;

import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.queue.menu.QueueSelectKitMenu;
import me.hcfalerts.practice.queue.menu.QueuesListMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class QueueListener implements Listener {

	@EventHandler(priority = EventPriority.LOW)
	public void onPlayerQuitEvent(PlayerQuitEvent event) {
		Profile profile = Profile.get(event.getPlayer().getUniqueId());

		if (profile.getState() == ProfileState.QUEUEING) {
			profile.getQueueProfile().getQueue().removePlayer(profile.getQueueProfile());
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteractEvent(PlayerInteractEvent event) {
		if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
			Profile profile = Profile.get(event.getPlayer().getUniqueId());
			if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
				HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

				if (hotbarItem != null) {
					boolean cancelled = false;

					if (hotbarItem == HotbarItem.QUEUE_JOIN_RANKED) {
						if (!profile.isBusy()) {
							new QueueSelectKitMenu(true).openMenu(event.getPlayer());
						}
					} else if (hotbarItem == HotbarItem.QUEUE_JOIN_UNRANKED) {
						if (!profile.isBusy()) {
							new QueueSelectKitMenu(false).openMenu(event.getPlayer());
						}
					} else if (hotbarItem == HotbarItem.QUEUE_LEAVE) {
						if (profile.getState() == ProfileState.QUEUEING) {
							profile.getQueueProfile().getQueue().removePlayer(profile.getQueueProfile());
						}
					} else if (hotbarItem == HotbarItem.QUEUES_MENU) {
						if (!profile.isBusy()) {
							new QueuesListMenu().openMenu(event.getPlayer());
						}
					} else {
						cancelled = true;
					}

					event.setCancelled(cancelled);
				}
			}
		}
	}

}
