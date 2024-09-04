package me.hcfalerts.practice.utilities.enchantment;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Glow extends Enchantment {

    public Glow(int id) {
        super(id);
    }


    public boolean canEnchantItem(ItemStack arg0) {
        return true;
    }


    public boolean conflictsWith(Enchantment arg0) {
        return false;
    }



    public int getMaxLevel() {
        return 5;
    }


    public String getName() {
        return "Glow";
    }


    public int getStartLevel() {
        return 0;
    }

    public int getId() {
        return 100;
    }

    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.ALL;
    }
}
