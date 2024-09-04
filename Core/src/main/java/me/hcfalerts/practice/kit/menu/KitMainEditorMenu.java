package me.hcfalerts.practice.kit.menu;

import com.google.common.collect.Maps;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class KitMainEditorMenu extends Menu {

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + "Main Editor";
    }

    @Override
    public int getSize() {
        return 9*5;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (Kit kits : Kit.getKits()) {
            buttons.put(buttons.size(), new Button() {
                @Override
                public ItemStack getButtonItem(Player player) {
                    return new ItemBuilder(kits.getDisplayIcon())
                            .name(CC.PINK + kits.getName())
                            .lore(CC.MENU_BAR, "&fEnabled&7: " + (kits.isEnabled() ? CC.GREEN + "Yes" : CC.RED + "No"), CC.MENU_BAR)
                            .build();
                }

                @Override
                public void clicked(Player player, ClickType clickType) {
                    player.closeInventory();
                    new KitEditorMenu(kits).openMenu(player);
                }
            });
        }

        return buttons;
    }
}
