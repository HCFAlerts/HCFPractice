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
public class KitSlotsMenu extends Menu {

    private final Kit kit;
    private final boolean ranked;

    {
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + kit.getName() + CC.GRAY + " »" + CC.DARK_GRAY + " Set Slot";
    }

    @Override
    public int getSize() {
        return 9*6;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        Button free = new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE).durability(5)
                        .name(CC.GREEN + "Free").lore("", CC.GRAY + "Click to select this slot")
                        .build();
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
                if (ranked) kit.setRankedSlot(slot);
                else kit.setUnrankedSlot(slot);
            }
        };
        Button busy = new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS_PANE).durability(15)
                        .name(CC.RED + "Busy").lore("", CC.GRAY + "This slot is occupied by another kit")
                        .build();
            }
        };

        for (int i = 0; i < 9*5; i++) {
            if (ranked) {
                for (Kit kit1 : Kit.getKits()) {
                    if (kit1.getRankedSlot() == i) {
                        buttons.put(i, busy);
                        break;
                    }
                }
            } else {
                for (Kit kit1 : Kit.getKits()) {
                    if (kit1.getUnrankedSlot() == i) {
                        buttons.put(i, busy);
                        break;
                    }
                }
            }

            buttons.put(i, free);
        }

        buttons.put(getSize() - 1, new BackButton(new KitRankedSelectMenu(kit)));

        return buttons;
    }
}
