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
public class KitHitDelayMenu extends Menu {

    private final Kit kit;

    {
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + kit.getName() + CC.GRAY + " »" + CC.DARK_GRAY + " Hit Delay";
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        buttons.put(9, new DecrementButton(kit, 10));
        buttons.put(10, new DecrementButton(kit, 5));
        buttons.put(11, new DecrementButton(kit, 1));

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.GOLD_SWORD)
                        .name(CC.PINK + "Hit Delay: " + CC.WHITE + kit.getGameRules().getHitDelay())
                        .build();
            }
        });

        buttons.put(15, new IncrementButton(kit, 1));
        buttons.put(16, new IncrementButton(kit, 5));
        buttons.put(17, new IncrementButton(kit, 10));

        buttons.put(26, new BackButton(new KitEditRulesMenu(kit)));

        return buttons;
    }

    @RequiredArgsConstructor
    private static class DecrementButton extends Button {

        private final Kit kit;
        private final int amount;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS)
                    .durability(14)
                    .name(CC.RED + "-" + amount)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setHitDelay(kit.getGameRules().getHitDelay() - amount);
        }
    }

    @RequiredArgsConstructor
    private static class IncrementButton extends Button {

        private final Kit kit;
        private final int amount;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS)
                    .durability(5)
                    .name(CC.RED + "+" + amount)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().setHitDelay(kit.getGameRules().getHitDelay() + amount);
        }
    }
}
