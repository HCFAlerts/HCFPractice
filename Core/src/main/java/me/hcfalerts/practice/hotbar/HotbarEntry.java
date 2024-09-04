package me.hcfalerts.practice.hotbar;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
@AllArgsConstructor
public class HotbarEntry {

    private final ItemStack itemStack;
    private final int slot;

}
