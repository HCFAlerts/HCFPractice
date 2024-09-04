package me.hcfalerts.practice.profile.meta.option.button;

import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.meta.option.menu.ProfileOptionButton;
import me.hcfalerts.practice.tablist.TabType;
import me.hcfalerts.practice.utilities.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

public class ChangeTabTypeOptionButton extends ProfileOptionButton {

    @Override
    public ItemStack getEnabledItem(Player player) {
        return new ItemBuilder(Material.CARPET).build();
    }

    @Override
    public ItemStack getDisabledItem(Player player) {
        return new ItemBuilder(Material.CARPET).build();
    }

    @Override
    public String getOptionName() {
        return "&d&lChange Tablist Style";
    }

    @Override
    public String getDescription() {
        return "If you don't like this Tablist mode you can change it.";
    }

    @Override
    public String getEnabledOption() {
        return "Modern Type";
    }

    @Override
    public String getDisabledOption() {
        return "Vanilla Type";
    }

    @Override
    public boolean isEnabled(Player player) {
        return Profile.get(player.getUniqueId()).getTabType() == TabType.MODERN;
    }

    @Override
    public void clicked(Player player, ClickType clickType) {
        Profile profile = Profile.get(player.getUniqueId());
        if (profile.getTabType() == TabType.MODERN) profile.setTabType(TabType.VANILLA);
        else profile.setTabType(TabType.MODERN);
    }
}
