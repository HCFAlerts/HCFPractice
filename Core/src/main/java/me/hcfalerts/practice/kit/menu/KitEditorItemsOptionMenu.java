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
public class KitEditorItemsOptionMenu extends Menu {

    private final Kit kit;

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + kit.getName() + CC.GRAY + " »" + CC.DARK_GRAY + " Select Option EditorItems";
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
                return new ItemBuilder(Material.CHEST)
                        .name(CC.PINK + "Give Editor Items")
                        .lore("", CC.GRAY + "The set inventory will be put into your inventory.")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                player.closeInventory();
                player.getInventory().setContents(kit.getEditRules().getEditorItems().toArray(new ItemStack[0]));
            }
        });

        buttons.put(15, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.ANVIL)
                        .name(CC.PINK + "Set Editor Items")
                        .lore("", CC.GRAY + "This will set the items you have in your inventory.")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getEditRules().getEditorItems().clear();
                for (ItemStack content : player.getInventory().getContents()) {
                    kit.getEditRules().getEditorItems().add(content);
                }
                player.sendMessage(CC.GREEN + "Editor Items set.");
            }
        });

        buttons.put(26, new BackButton(new KitEditRulesMenu(kit)));

        return buttons;
    }
}
