package me.hcfalerts.practice.profile.modmode;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ModModeListener implements Listener {

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() != null && (event.getAction() == Action.RIGHT_CLICK_AIR ||
                event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            Profile profile = Profile.get(player.getUniqueId());
            HotbarItem hotbarItem = Hotbar.fromItemStack(event.getItem());

            if (hotbarItem != null) {
                if (hotbarItem == HotbarItem.RANDOM_TELEPORT) {
                    event.setCancelled(true);
                    List<Player> players = new ArrayList<>();
                    for (Player player1 : Bukkit.getOnlinePlayers()) {
                        if (Profile.get(player1.getUniqueId()).getState() == ProfileState.FIGHTING) {
                            players.add(player1);
                        }
                    }

                    if (players.isEmpty()) {
                        new MessageFormat(Locale.STAFF_MODE_NO_PLAYERS_SPEC.format(profile.getLocale()))
                                .send(player);
                    } else {
                        Player target = players.get(ThreadLocalRandom.current().nextInt(players.size()));
                        Profile targetProfile = Profile.get(target.getUniqueId());

                        if (profile.getMatch() != null) {
                            profile.setMatch(null);
//                            TaskUtil.runLater(() -> targetProfile.getMatch().addSpectator(player, target, false), 5L);
//                            return;
                        }

                        targetProfile.getMatch().addSpectator(player, target, false);
                    }
                }
                else if (hotbarItem == HotbarItem.HIDE_ALL_PLAYERS) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                        player.hidePlayer(onlinePlayer);
                    }
                    new MessageFormat(Locale.STAFF_MODE_NOW_SHOW_ALL_PLAYERS.format(profile.getLocale()))
                            .send(player);
                }
                else if (hotbarItem == HotbarItem.RESET) {
                    if (profile.getMatch() != null) profile.setMatch(null);
                    HCFPractice.get().getEssentials().teleportToSpawn(player);
                    for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                        VisibilityLogic.handle(player, otherPlayer);
                        VisibilityLogic.handle(otherPlayer, player);
                    }
                    new MessageFormat(Locale.STAFF_MODE_NOW_IN_SPAWN.format(profile.getLocale()))
                            .send(player);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player) {
            Player player = (Player) event.getDamager();
            Profile profile = Profile.get(player.getUniqueId());
            if (profile.getState() == ProfileState.STAFF_MODE) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.getState() == ProfileState.STAFF_MODE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.getState() == ProfileState.STAFF_MODE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ModMode.getStaffmode().remove(player.getUniqueId());
    }
}
