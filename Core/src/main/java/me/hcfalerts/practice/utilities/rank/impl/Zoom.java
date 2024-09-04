package me.hcfalerts.practice.utilities.rank.impl;

import club.frozed.core.ZoomAPI;
import me.hcfalerts.practice.utilities.rank.IRank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Zoom implements IRank {

    @Override
    public String getRankSystem() {
        return "ZoomCore";
    }

    @Override
    public String getName(UUID uuid) {
        return ZoomAPI.getRankName(getPlayer(uuid));
    }

    @Override
    public String getPrefix(UUID uuid) {
        return ZoomAPI.getRankPrefix(getPlayer(uuid));
    }

    @Override
    public String getSuffix(UUID uuid) {
        return ZoomAPI.getRankSuffix(getPlayer(uuid));
    }

    @Override
    public String getColor(UUID uuid) {
        return ZoomAPI.getRankColor(getPlayer(uuid)) + ZoomAPI.getRankName(getPlayer(uuid));
    }

    @Override
    public int getWeight(UUID uuid) {
        return 0;
    }

    public Player getPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
}
