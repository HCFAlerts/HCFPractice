package me.hcfalerts.practice.leaderboard.menu.button;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.elo.EloUtil;
import me.hcfalerts.practice.utilities.menu.Button;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

@AllArgsConstructor
public class StatsButton extends Button {

    public Player target;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = Lists.newArrayList();
        Profile profile = Profile.get(target.getUniqueId());

        for (String s : HCFPractice.get().getLeaderboardConfig().getStringList("CUSTOM_ITEMS.PERSONAL_STATS.DESCRIPTION")) {
            if (s.contains("{kits}")) {
                for (Kit kit : Kit.getKits()) {
                    if (!kit.getGameRules().isRanked()) continue;
                    lore.add(HCFPractice.get().getLeaderboardConfig().getString("CUSTOM_ITEMS.PERSONAL_STATS.KITS_FORMAT")
                            .replace("{kit}", kit.getName())
                            .replace("{category}", String.valueOf(profile.getCategory().getDisplayName()))
                            .replace("{color}", profile.getColor())
                            .replace("{data}", String.valueOf(profile.getKitData().get(kit).getElo())));
                }
                continue;
            }
            lore.add(s
                    .replace("{bars}", CC.MENU_BAR)
                    .replace("{elo}", String.valueOf(EloUtil.getGlobalElo(profile))));
        }

        ItemStack item = new ItemBuilder(Material.SKULL_ITEM)
                .durability(3)
                .name(HCFPractice.get().getLeaderboardConfig().getString("CUSTOM_ITEMS.PERSONAL_STATS.TITLE")
                        .replace("{color}", profile.getColor())
                        .replace("{category}", String.valueOf(profile.getCategory().getDisplayName()))
                        .replace("{name}", target.getName()))
                .lore(lore)
                .build();

        SkullMeta itemMeta = (SkullMeta)item.getItemMeta();
        itemMeta.setOwner(target.getName());
        item.setItemMeta(itemMeta);
        return item;
    }
}
