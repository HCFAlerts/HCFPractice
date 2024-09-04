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
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.Map;

@RequiredArgsConstructor
public class KitStatusMenu extends Menu {

    private final Kit kit;

    @Override
    public String getTitle(Player player) {
        return CC.DARK_GRAY + kit.getName() + CC.GRAY + " »" + CC.DARK_GRAY + " Status";
    }

    @Override
    public int getSize() {
        return 9*3;
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        buttons.put(13, new Button() {
            @Override
            public ItemStack getButtonItem(Player player) {
                StringBuilder effects = new StringBuilder();
                for (PotionEffect effect : kit.getGameRules().getEffects()) {
                    effects.append(CC.DARK_GREEN).append(effect.getType().getName()).append(CC.GRAY).append(":")
                            .append(CC.DARK_PURPLE).append(effect.getAmplifier()).append(CC.GRAY).append(":")
                            .append(CC.AQUA).append(effect.getDuration()).append(CC.WHITE).append(", ");
                }
                return new ItemBuilder(Material.EMPTY_MAP)
                        .name("&d&lStatus &7(&a" + kit.getName() + "&7)")
                        .lore(
                                "&fRanked &7» " + (kit.getGameRules().isRanked() ? "&aYes" : "&cNo"),
                                "&fBuild &7» " + (kit.getGameRules().isBuild() ? "&aYes" : "&cNo"),
                                "&fSpleef &7» " + (kit.getGameRules().isSpleef() ? "&aYes" : "&cNo"),
                                "&fSumo &7» " + (kit.getGameRules().isSumo() ? "&aYes" : "&cNo"),
                                "&fParkour &7» " + (kit.getGameRules().isParkour() ? "&aYes" : "&cNo"),
                                "&fHCF &7» " + (kit.getGameRules().isHcf() ? "&aYes" : "&cNo"),
                                "&fBridge &7» " + (kit.getGameRules().isBridge() ? "&aYes" : "&cNo"),
                                "&fBoxing &7» " + (kit.getGameRules().isBoxing() ? "&aYes" : "&cNo"),
                                "&fSkyWars &7» " + (kit.getGameRules().isSkywars() ? "&aYes" : "&cNo"),
                                "&fHCFTrap &7» " + (kit.getGameRules().isHcfTrap() ? "&aYes" : "&cNo"),
                                "&fHealth Regeneration &7» " + (kit.getGameRules().isHealthRegeneration() ? "&aYes" : "&cNo"),
                                "&fShow Health &7» " + (kit.getGameRules().isShowHealth() ? "&aYes" : "&cNo"),
                                "&fHit Delay &7» &c" + kit.getGameRules().getHitDelay(),
                                "&fKB Profile &7» &f" + (kit.getGameRules().getKbProfile().isEmpty() ? "Default" : kit.getGameRules().getKbProfile()),
                                "&fEffects &7(&fEffect&7:&fAmplifier&7:&fDuration&7) » "
                                        + (kit.getGameRules().getEffects().isEmpty() ? "None" : effects.substring(0, effects.toString().length() - 2))
                        )
                        .build();
            }
        });


        buttons.put(26, new BackButton(new KitEditorMenu(kit)));
        return buttons;
    }
}
