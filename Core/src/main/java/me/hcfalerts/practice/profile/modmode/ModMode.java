package me.hcfalerts.practice.profile.modmode;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.Getter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.PlayerUtil;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import me.hcfalerts.practice.utilities.string.TPSUtil;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ModMode {

    @Getter public static Set<UUID> staffmode = Sets.newConcurrentHashSet();

    public static void add(Player player) {
        Profile profile = Profile.get(player.getUniqueId());

        profile.setState(ProfileState.STAFF_MODE);

        Hotbar.giveHotbarItems(player);

        player.setGameMode(GameMode.CREATIVE);
        player.setAllowFlight(true);
        player.setFlying(true);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            VisibilityLogic.handle(player, otherPlayer);
            VisibilityLogic.handle(otherPlayer, player);
        }

        staffmode.add(player.getUniqueId());

        new MessageFormat(Locale.STAFF_MODE_JOIN_STAFF.format(profile.getLocale()))
                .send(player);
    }

    public static void remove(Player player) {
        Profile profile = Profile.get(player.getUniqueId());

        profile.setState(ProfileState.LOBBY);
        if (profile.getMatch() != null) profile.setMatch(null);

        PlayerUtil.reset(player);
        Hotbar.giveHotbarItems(player);
        HCFPractice.get().getEssentials().teleportToSpawn(player);

        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            VisibilityLogic.handle(player, otherPlayer);
            VisibilityLogic.handle(otherPlayer, player);
        }

        staffmode.remove(player.getUniqueId());

        new MessageFormat(Locale.STAFF_MODE_LEAVE_STAFF.format(profile.getLocale()))
                .send(player);
    }

    public static List<String> getScoreboardLines(Player player) {
        List<String> lines = Lists.newArrayList();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.getMatch() != null) {
            Match match = profile.getMatch();
            for (String s : HCFPractice.get().getScoreboardConfig().getStringList("BOARD.STAFFMODE.SPECTATING")) {
                lines.add(s
                        .replace("{players}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("{staffs}", String.valueOf(PlayerUtil.getStaffCount()))
                        .replace("{in-fight}", String.valueOf(HCFPractice.get().getInFightsTotal()))
                        .replace("{tps}", TPSUtil.getTPS())
                        .replace("{match-players}", String.valueOf(match.getParticipants().size()))
                        .replace("{match-duration}", match.getDuration())
                        .replace("{match-state}", match.getState().name())
                        .replace("{match-ranked}", match.isRanked() ? "&aTrue" : "&cFalse"));
            }
        } else {
            for (String s : HCFPractice.get().getScoreboardConfig().getStringList("BOARD.STAFFMODE.LOBBY")) {
                lines.add(s
                        .replace("{players}", String.valueOf(Bukkit.getOnlinePlayers().size()))
                        .replace("{staffs}", String.valueOf(PlayerUtil.getStaffCount()))
                        .replace("{in-fight}", String.valueOf(HCFPractice.get().getInFightsTotal()))
                        .replace("{tps}", TPSUtil.getTPS()));
            }
        }
        return lines;
    }


}
