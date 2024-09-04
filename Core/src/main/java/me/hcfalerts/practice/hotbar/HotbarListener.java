package me.hcfalerts.practice.hotbar;

import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class HotbarListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

            if (hotbarItem != null) {
                event.setCancelled(true);
                if (hotbarItem.getCommand() != null) {
                    player.chat("/" + hotbarItem.getCommand());
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player = (Player) event.getDamager();
            Profile profile = Profile.get(player.getUniqueId());
            if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
                if (Hotbar.getItem(Hotbar.fromItemStack(player.getItemInHand())) != null) {
                    player.setItemInHand(Hotbar.getItem(Hotbar.fromItemStack(player.getItemInHand())));
                    player.updateInventory();
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
            if (Hotbar.getItem(Hotbar.fromItemStack(player.getItemInHand())) != null) {
                player.setItemInHand(Hotbar.getItem(Hotbar.fromItemStack(player.getItemInHand())));
                player.updateInventory();
            }
        }
    }
}
