package me.hcfalerts.practice.profile.deatheffects.menu;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.deatheffects.Data;
import me.hcfalerts.practice.profile.deatheffects.DeathEffect;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class DeathEffectMenu extends Menu {

    private final BasicConfigurationFile config = HCFPractice.get().getDeathEffectsInvConfig();

    public DeathEffectMenu() {
        super();
        setPlaceholder(true);
    }

    @Override
    public String getTitle(Player player) {
        return config.getString("TITLE");
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        Profile profile = Profile.get(player.getUniqueId());

        int slot = 10;
        for (Data deathEffect : DeathEffect.getDeathEffects()) {
            buttons.put(slot++, new DeathEffectButton(deathEffect, profile));
            slot++;
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private static class DeathEffectButton extends Button {

        private final BasicConfigurationFile config = HCFPractice.get().getDeathEffectsInvConfig();

        private final Data deathEffectInstance;
        private final Profile profile;

        @Override
        public ItemStack getButtonItem(Player player) {
            List<String> lore = Lists.newArrayList();
            String path = deathEffectInstance.getClass().getSimpleName().toUpperCase() + "_ITEM";
            boolean acquired = player.hasPermission("practice.deatheffects." + deathEffectInstance.getClass().getSimpleName().toLowerCase());
            boolean using = (acquired && profile.getDeathEffect() != null &&
                    profile.getDeathEffect().getClass().getSimpleName().equals(deathEffectInstance.getClass().getSimpleName()));
            if (acquired) {
                for (String s : this.config.getStringList(path + ".LORE.ACQUIRED")) {
                    lore.add(CC.translate(s
                            .replace("{used}", using ?
                                config.getString("VARIABLES.USING") :
                                config.getString("VARIABLES.NOT_USING"))));
                }
            } else {
                for (String s : this.config.getStringList(path + ".LORE.NORMAL")) {
                    lore.add(CC.translate(s));
                }
            }
            return new ItemBuilder(deathEffectInstance.getItemStack())
                    .name(acquired ? this.config.getString(path + ".NAME.ACQUIRED") : this.config.getString(path + ".NAME.NORMAL"))
                    .lore(lore)
                    .build();
        }

        @Override
        public void clicked(Player player, ClickType clickType) {
            if (profile.getDeathEffect() != null && profile.getDeathEffect().getClass().getSimpleName().equals(deathEffectInstance.getClass().getSimpleName())) {
                new MessageFormat(Locale.DEATH_EFFECT_ALREADY_USING
                            .format(profile.getLocale()))
                        .send(player);
            } else {
                try {
                    profile.setDeathEffect(deathEffectInstance.getClass().getConstructor(Profile.class).newInstance(profile));
                    new MessageFormat(Locale.DEATH_EFFECT_NOW_USING
                            .format(profile.getLocale()))
                            .add("{death-effect}",
                                    this.config.getString(deathEffectInstance.getClass().getSimpleName().toUpperCase() + "_ITEM.NAME.NORMAL"))
                            .send(player);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
