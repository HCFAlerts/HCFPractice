package me.hcfalerts.practice.utilities.item;

import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

@UtilityClass
public class ItemUtil {

    public ItemStack getItemStack(String config) {
        String[] deserialized = config.split(":");
        return new ItemBuilder(deserialized[0])
                .data(Integer.parseInt(deserialized[1]))
                .build();
    }
}
