package me.hcfalerts.practice.leaderboard.menu.button;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.leaderboard.Leaderboard;
import me.hcfalerts.practice.leaderboard.entry.LeaderboardKitsEntry;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.meta.ProfileKitData;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class KitButton extends Button {

    private final Kit kit;

    @Override
    public ItemStack getButtonItem(Player player) {
        List<String> lore = Lists.newArrayList();
        List<LeaderboardKitsEntry> leaderboard = new ArrayList<>();
        long limit = 10;
        for (LeaderboardKitsEntry kitsEntry : Leaderboard.getKitLeaderboards().get(kit.getName())) {
            if (limit-- == 0) break;
            leaderboard.add(kitsEntry);
        }

        int pos = 0;

        lore.add(CC.MENU_BAR);
        for (LeaderboardKitsEntry leaderboardKitsEntry : leaderboard) {
            pos++;
            Profile profile = leaderboardKitsEntry.getProfile();
            if (profile != null) {
                if (pos == 1) {
                    List<String> first = HCFPractice.get().getLeaderboardConfig().getStringList("CUSTOM_ITEMS.KIT.POSITIONS.1");
                    for (String s : first) {
                        lore.add(s
                                .replace("{pos}", String.valueOf(pos))
                                .replace("{category}", String.valueOf(profile.getCategory().getDisplayName()))
                                .replace("{name}", profile.getName())
                                .replace("{color}", profile.getColor())
                                .replace("{data}", String.valueOf(profile.getKitData().getOrDefault(kit, new ProfileKitData()).getElo()))
                                .replace("{bars}", CC.MENU_BAR));
                    }
                } else if (pos == 2) {
                    List<String> second = HCFPractice.get().getLeaderboardConfig().getStringList("CUSTOM_ITEMS.KIT.POSITIONS.2");
                    for (String s : second) {
                        lore.add(s
                                .replace("{pos}", String.valueOf(pos))
                                .replace("{category}", String.valueOf(profile.getCategory().getDisplayName()))
                                .replace("{name}", profile.getName())
                                .replace("{color}", profile.getColor())
                                .replace("{data}", String.valueOf(profile.getKitData().getOrDefault(kit, new ProfileKitData()).getElo()))
                                .replace("{bars}", CC.MENU_BAR));
                    }
                } else if (pos == 3) {
                    List<String> third = HCFPractice.get().getLeaderboardConfig().getStringList("CUSTOM_ITEMS.KIT.POSITIONS.3");
                    for (String s : third) {
                        lore.add(s
                                .replace("{pos}", String.valueOf(pos))
                                .replace("{category}", String.valueOf(profile.getCategory().getDisplayName()))
                                .replace("{name}", profile.getName())
                                .replace("{color}", profile.getColor())
                                .replace("{data}", String.valueOf(profile.getKitData().getOrDefault(kit, new ProfileKitData()).getElo()))
                                .replace("{bars}", CC.MENU_BAR));
                    }
                } else {
                    List<String> another = HCFPractice.get().getLeaderboardConfig().getStringList("CUSTOM_ITEMS.KIT.POSITIONS.ANOTHER");
                    for (String s : another) {
                        lore.add(s
                                .replace("{pos}", String.valueOf(pos))
                                .replace("{category}", String.valueOf(profile.getCategory().getDisplayName()))
                                .replace("{name}", profile.getName())
                                .replace("{color}", profile.getColor())
                                .replace("{data}", String.valueOf(profile.getKitData().getOrDefault(kit, new ProfileKitData()).getElo()))
                                .replace("{bars}", CC.MENU_BAR));
                    }
                }
            }
        }
        lore.add(CC.MENU_BAR);

        return new ItemBuilder(kit.getDisplayIcon().getType())
                .name(HCFPractice.get().getLeaderboardConfig().getString("CUSTOM_ITEMS.KIT.TITLE").replace("{kit}", kit.getName()))
                .durability(kit.getDisplayIcon().getDurability())
                .lore(lore)
                .build();
    }
}
