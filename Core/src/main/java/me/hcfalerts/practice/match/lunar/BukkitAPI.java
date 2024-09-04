package me.hcfalerts.practice.match.lunar;

import com.google.common.collect.Maps;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketTeammates;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.experimental.UtilityClass;
import me.hcfalerts.practice.profile.Profile;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.Map;
import java.util.UUID;

@UtilityClass
public class BukkitAPI {

    public void sendWaypoint(Player player, Location location) {
        LunarClientAPI.getInstance().sendWaypoint(player, new LCWaypoint("Rally", location, Color.GREEN.getGreen(), true, true));
    }

    public void removeWaypoint(Player player, Location location) {
        LunarClientAPI.getInstance().removeWaypoint(player, new LCWaypoint("Rally", location, Color.GREEN.getGreen(), true, true));
    }

    public void sendTeammates(Player player, Player target) {
        Map<UUID, Map<String, Double>> data = Maps.newHashMap();
        Map<String, Double> map = Maps.newHashMap();
        map.put("focus", 100.0);
        data.put(target.getUniqueId(), map);
        LunarClientAPI.getInstance().sendTeammates(player,
                new LCPacketTeammates(target.getUniqueId(), System.currentTimeMillis(), data));
    }

    public void removeTeammates(Player player) {
        LunarClientAPI.getInstance().sendTeammates(player,
                new LCPacketTeammates(Profile.get(player.getUniqueId()).getFocused().getUniqueId(), System.currentTimeMillis(), Maps.newHashMap()));
    }

    public boolean isRunning(Player player) {
        return LunarClientAPI.getInstance().isRunningLunarClient(player);
    }
}
