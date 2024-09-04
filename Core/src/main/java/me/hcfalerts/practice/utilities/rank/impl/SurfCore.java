package me.hcfalerts.practice.utilities.rank.impl;

import com.skitbet.surfapi.SurfCoreAPI;
import me.hcfalerts.practice.utilities.rank.IRank;
import org.bukkit.ChatColor;

import java.util.UUID;

public class SurfCore implements IRank {
    @Override
    public String getRankSystem() {
        return "SurfCore";
    }

    @Override
    public String getName(UUID uuid) {
        return SurfCoreAPI.getInstance().getProfileManager().getProfileRankName(uuid);
    }

    @Override
    public String getPrefix(UUID uuid) {
        return SurfCoreAPI.getInstance().getProfileManager().getProfileRankPrefix(uuid);
    }

    @Override
    public String getSuffix(UUID uuid) {
        return SurfCoreAPI.getInstance().getProfileManager().getProfileSuffix(uuid);
    }

    @Override
    public String getColor(UUID uuid) {
        return ChatColor.WHITE.toString();
    }

    @Override
    public int getWeight(UUID uuid) {
        return SurfCoreAPI.getInstance().getProfileManager().getProfileRankPriority(uuid);
    }
}
