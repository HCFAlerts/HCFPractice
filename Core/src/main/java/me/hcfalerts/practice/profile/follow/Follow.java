package me.hcfalerts.practice.profile.follow;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Follow {

    @Getter public static Map<UUID, Follow> follows = Maps.newHashMap();

    private final UUID follower;

    private final UUID followed;
    private final Player followedPlayer;

    public static Follow getByFollowed(UUID followed) {
        for (Follow f : follows.values()) {
            if (f.getFollowed().equals(followed)) {
                return f;
            }
        }
        return null;
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(follower);
    }

    public void follow() {
        Player player = getPlayer();
        Profile targetProfile = Profile.get(followed);

        detect();

        player.getInventory().clear();
        player.getInventory().setItem(Hotbar.getSlot(HotbarItem.FOLLOW), Hotbar.getItem(HotbarItem.FOLLOW));
        player.updateInventory();
    }

    public void detect() {
        Player followerPlayer = getPlayer();
        Profile targetProfile = Profile.get(followed);
        if (targetProfile.getState() == ProfileState.LOBBY || targetProfile.getState() == ProfileState.QUEUEING) {
            followerPlayer.showPlayer(followedPlayer);
            followerPlayer.teleport(followedPlayer.getLocation());
        }
        else if (targetProfile.getState() == ProfileState.FIGHTING || targetProfile.getState() == ProfileState.SPECTATING) {
            targetProfile.getMatch().addSpectator(followerPlayer, followedPlayer, true);
        }
        else {
            followerPlayer.sendMessage("§cYou cannot follow them.");
        }
    }

    public List<String> getScoreboardLines() {
        BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();
        List<String> lines = config.getStringList("BOARD.FOLLOWING");

        lines.replaceAll(line -> line
                .replace("{color}", Profile.get(followed).getColor())
                .replace("{player}", followedPlayer.getName()));

        return lines;
    }
}
