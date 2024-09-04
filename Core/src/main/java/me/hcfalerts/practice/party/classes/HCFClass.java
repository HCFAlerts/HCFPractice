package me.hcfalerts.practice.party.classes;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.utilities.PotionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@AllArgsConstructor
public enum HCFClass {
    ARCHER("Archer",
            PotionUtil.convertStringToListPotionEffect(HCFPractice.get().getMainConfig().getStringList("PARTY.CLASS_EFFECTS.ARCHER")),
            new Material[] {
                    Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE,
                    Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS}),
    ROGUE("Rogue",
            PotionUtil.convertStringToListPotionEffect(HCFPractice.get().getMainConfig().getStringList("PARTY.CLASS_EFFECTS.ROGUE")),
            new Material[] {
                    Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE,
                    Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS}),
    BARD("Bard",
            PotionUtil.convertStringToListPotionEffect(HCFPractice.get().getMainConfig().getStringList("PARTY.CLASS_EFFECTS.BARD")),
            new Material[] {
                    Material.GOLD_HELMET, Material.GOLD_CHESTPLATE,
                    Material.GOLD_LEGGINGS, Material.GOLD_BOOTS});

    final public static Map<UUID, HCFClass> classMap = Maps.newHashMap();

    private final String name;
    private final List<PotionEffect> effects;
    private final Material[] armor;

    public boolean isApply(Player player) {
        ItemStack[] armorContents = player.getInventory().getArmorContents();
        return armorContents[3].getType() == armor[0] &&
                armorContents[2].getType() == armor[1] &&
                armorContents[1].getType() == armor[2] &&
                armorContents[0].getType() == armor[3];
    }

    public void equip(Player player) {
        if (classMap.containsKey(player.getUniqueId())) return;
        classMap.put(player.getUniqueId(), this);
        for (PotionEffect effect : effects) {
            player.addPotionEffect(effect);
        }
    }

}


