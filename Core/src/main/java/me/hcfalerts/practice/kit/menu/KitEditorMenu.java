package me.hcfalerts.practice.kit.menu;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitEditorData;
import me.hcfalerts.practice.profile.Profile;
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
public class KitEditorMenu extends Menu {

    private final Kit kit;

    {
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + "Editing " + CC.GRAY + "» " + CC.DARK_GRAY + kit.getName();
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        // Loadouts option
        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.CHEST)
                        .name("&dLoadout")
                        .lore(CC.MENU_BAR, "&7Right-Click to open Loadout Menu", CC.MENU_BAR)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitLoadoutsMenu(kit).openMenu(player);
            }
        });

        // Rules option
        buttons.put(11, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BOOK)
                        .name("&dRules")
                        .lore(CC.MENU_BAR, "&7Right-Click to edit Kit Rules", CC.MENU_BAR)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitEditRulesMenu(kit).openMenu(player);
            }
        });

        // Rename option
        buttons.put(12, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.NAME_TAG)
                        .name("&dRename")
                        .lore(CC.MENU_BAR, "&7Right-Click to Rename the Kit", CC.MENU_BAR)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                Profile.get(player.getUniqueId()).setKitEditorStatus(new KitEditorData(kit));
                player.closeInventory();
                player.sendMessage(CC.PINK + "Insert new kit name.");
                player.sendMessage(CC.PINK + "If you need to cancel this process just write \"cancel\" in the chat");
            }
        });

        // Set Slots option
        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.WOOD_BUTTON)
                        .name("&dSet Slots")
                        .lore(CC.MENU_BAR, "&7Right-Click to open Slots Menu", CC.MENU_BAR)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                player.closeInventory();
                new KitRankedSelectMenu(kit).openMenu(player);
            }
        });

        // Toggle option
        buttons.put(14, new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.LEVER)
                        .name("&dToggle")
                        .lore(CC.MENU_BAR, "&fEnabled &7» " + (kit.isEnabled() ? CC.GREEN + "Yes" : CC.RED + "No"), CC.MENU_BAR)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.setEnabled(!kit.isEnabled());
            }
        });

        buttons.put(15, new Button() {

            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.ITEM_FRAME)
                        .name("&dSet Icon")
                        .lore(CC.MENU_BAR, "&7Right-Click to open Icon Menu", CC.MENU_BAR)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitSetIconMenu(kit).openMenu(player);
            }
        });

        // Status option
        buttons.put(16, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.INK_SACK)
                        .name("&dStatus")
                        .durability(kit.isEnabled() ? 10 : 1)
                        .lore(CC.MENU_BAR, "&7Right-Click to open Status Menu", CC.MENU_BAR)
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                player.closeInventory();
                new KitStatusMenu(kit).openMenu(player);
            }
        });

        buttons.put(26, new BackButton(new KitMainEditorMenu()));
        return buttons;
    }
}
