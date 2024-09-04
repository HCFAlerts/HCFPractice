package me.hcfalerts.practice.kit.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
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
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Map;

@AllArgsConstructor
public class KitEditEffectsMenu extends Menu {
    
    private final Kit kit;

    {
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + kit.getName() + CC.GRAY + " »" + CC.DARK_GRAY + " Effects";
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (PotionType value : PotionType.values()) {
            if (value == PotionType.WATER) continue;
            buttons.put(buttons.size(), new EffectButton(value, kit));
        }

        buttons.put(getSize() - 1, new BackButton(new KitEditRulesMenu(kit)));
        return buttons;
    }

    @RequiredArgsConstructor
    private static class EffectButton extends Button {

        private final PotionType type;
        private final Kit kit;

        @Override
        public ItemStack getButtonItem(Player player) {
            Material material = Material.GLASS_BOTTLE;
            for (PotionEffect effect : kit.getGameRules().getEffects()) {
                if (effect.getType().equals(type.getEffectType())) {
                    material = Material.POTION;
                }
            }
            return new ItemBuilder(material)
                    .name(CC.WHITE + type.getEffectType().getName())
                    .lore("", "&7Left Click to add or edit", "&7Right Click to remove")
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (clickType == ClickType.RIGHT) {
                kit.getGameRules().getEffects().removeIf(potionEffect -> potionEffect.getType().equals(type.getEffectType()));
            }
            else if (clickType == ClickType.LEFT) {
                for (PotionEffect effect : kit.getGameRules().getEffects()) {
                    if (effect.getType().equals(type.getEffectType())) {
                        new KitModifyEffectMenu(kit, effect).openMenu(player);
                        return;
                    }
                }
                PotionEffect potionEffect = new PotionEffect(type.getEffectType(), 60, 1);
                kit.getGameRules().getEffects().add(potionEffect);
                new KitModifyEffectMenu(kit, potionEffect).openMenu(player);
            }
        }
    }
}
