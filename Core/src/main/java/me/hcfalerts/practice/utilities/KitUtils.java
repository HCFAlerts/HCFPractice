package me.hcfalerts.practice.utilities;

import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.profile.Profile;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.hcfalerts.practice.utilities.InventoryUtil.leatherArmor;

public class KitUtils {

    public static void giveBridgeKit(Player player){
        Profile profile = Profile.get(player.getUniqueId());
        BasicTeamMatch teamMatch = (BasicTeamMatch) profile.getMatch();
        ItemStack[] armorRed = leatherArmor(Color.RED);
        ItemStack[] armorBlue = leatherArmor(Color.BLUE);
        if (teamMatch.getParticipantA().containsPlayer(player.getUniqueId())) {
            player.getInventory().setArmorContents(armorRed);
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(14).amount(64).build());
            });
        } else {
            player.getInventory().setArmorContents(armorBlue);
            player.getInventory().all(Material.STAINED_CLAY).forEach((key, value) -> {
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
                player.getInventory().setItem(key, new ItemBuilder(Material.STAINED_CLAY).durability(11).amount(64).build());
            });
        }
        player.updateInventory();
    }

}