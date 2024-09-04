package me.hcfalerts.practice.kit.menu;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.menu.button.BackButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class KitRankedSelectMenu extends Menu {

    private final Kit kit;

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + kit.getName() + CC.GRAY + " »" + CC.DARK_GRAY + " Select Ranked";
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.IRON_SWORD)
                        .name(CC.PINK + "UnRanked")
                        .lore("", CC.GRAY + "Select this to edit UnRanked slot menu.")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitSlotsMenu(kit, false).openMenu(player);
            }
        });

        buttons.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.DIAMOND_SWORD)
                        .name(CC.PINK + "Ranked")
                        .lore("", CC.GRAY + "Select this to edit Ranked slot menu.")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitSlotsMenu(kit, true).openMenu(player);
            }
        });

        buttons.put(26, new BackButton(new KitEditorMenu(kit)));

        return buttons;
    }
}
