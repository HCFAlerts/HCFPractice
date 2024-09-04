package me.hcfalerts.practice.kit.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.menu.button.BackButton;
import me.hcfalerts.practice.utilities.string.TimeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

@AllArgsConstructor
public class KitModifyEffectMenu extends Menu {

    private final Kit kit;
    private PotionEffect effect;

    {
        setUpdateAfterClick(true);
    }

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + "Editing " + effect.getType().getName() + " effect to " + kit.getName() + " Kit";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (PotionEffect value : kit.getGameRules().getEffects()) {
            if (value.getType().equals(effect.getType())) {
                effect = value;
                break;
            }
        }

        buttons.put(9, new DecrementButton(kit, effect, "1", 1, true));
        buttons.put(10, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.GLOWSTONE_DUST)
                        .name(CC.PINK + "Amplifier: " + CC.GRAY + effect.getAmplifier())
                        .build();
            }
        });
        buttons.put(11, new IncreaseButton(kit, effect, "1", 1, true));

        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.BREWING_STAND_ITEM)
                        .name("&aConfirm and Back")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                new KitEditEffectsMenu(kit).openMenu(player);
            }
        });

        buttons.put(6, new DecrementButton(kit, effect, "1s", 1, false));
        buttons.put(15, new DecrementButton(kit, effect, "10s", 10, false));
        buttons.put(24, new DecrementButton(kit, effect, "30s", 30, false));

        buttons.put(7, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS)
                        .durability(4)
                        .name(CC.YELLOW + "Reset")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().getEffects().removeIf(potionEffect -> potionEffect.getType().equals(effect.getType()));
                effect = new PotionEffect(effect.getType(), 60, 1);
                kit.getGameRules().getEffects().add(effect);
            }
        });

        buttons.put(16, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.REDSTONE)
                        .name(CC.PINK + "Duration: " + CC.GRAY + TimeUtils.formatIntoDetailedString(effect.getDuration(), Profile.get(player.getUniqueId()).getLocale()))
                        .build();
            }
        });

        buttons.put(25, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                return new ItemBuilder(Material.STAINED_GLASS)
                        .durability(4)
                        .name(CC.YELLOW + "Infinity Duration")
                        .build();
            }

            @Override
            public void clicked(Player player, ClickType clickType) {
                kit.getGameRules().getEffects().removeIf(potionEffect -> potionEffect.getType().equals(effect.getType()));
                effect = new PotionEffect(effect.getType(), Integer.MAX_VALUE, effect.getAmplifier());
                kit.getGameRules().getEffects().add(effect);
            }
        });

        buttons.put(8, new IncreaseButton(kit, effect, "1s", 1, false));
        buttons.put(17, new IncreaseButton(kit, effect, "10s", 10, false));
        buttons.put(26, new IncreaseButton(kit, effect, "30s", 30, false));

        buttons.put(22, new BackButton(new KitEditEffectsMenu(kit)));

        return buttons;
    }

    @AllArgsConstructor
    public static class IncreaseButton extends Button {

        private final Kit kit;
        private PotionEffect effect;
        private final String format;
        private final int amount;
        private final boolean power;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS)
                    .durability(5)
                    .name(CC.GREEN + "+" + format)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {

            kit.getGameRules().getEffects().removeIf(potionEffect -> potionEffect.getType().equals(effect.getType()));
            effect = new PotionEffect(effect.getType(),
                    (power ? effect.getDuration() : effect.getDuration() + amount),
                    (power ? effect.getAmplifier() + amount : effect.getAmplifier()));
            kit.getGameRules().getEffects().add(effect);
        }
    }

    @AllArgsConstructor
    public static class DecrementButton extends Button {

        private final Kit kit;
        private PotionEffect effect;
        private final String format;
        private final int amount;
        private final boolean power;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.STAINED_GLASS)
                    .durability(14)
                    .name(CC.RED + "-" + format)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            kit.getGameRules().getEffects().removeIf(potionEffect -> potionEffect.getType().equals(effect.getType()));
            effect = new PotionEffect(effect.getType(),
                    (power ? effect.getDuration() : effect.getDuration() - amount),
                    (power ? effect.getAmplifier() - amount : effect.getAmplifier()));
            kit.getGameRules().getEffects().add(effect);
        }
    }
}
