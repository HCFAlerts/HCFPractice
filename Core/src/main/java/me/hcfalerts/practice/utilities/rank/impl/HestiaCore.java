package me.hcfalerts.practice.utilities.rank.impl;

import me.quartz.hestia.HestiaAPI;
import me.hcfalerts.practice.utilities.rank.IRank;

import java.util.UUID;

public class HestiaCore implements IRank {

    @Override
    public String getRankSystem() {
        return "HestiaCore";
    }

    @Override
    public String getName(UUID uuid) {
        return HestiaAPI.instance.getRank(uuid);
    }

    @Override
    public String getPrefix(UUID uuid) {
        return HestiaAPI.instance.getRankPrefix(uuid);
    }

    @Override
    public String getSuffix(UUID uuid) {
        return HestiaAPI.instance.getRankSuffix(uuid);
    }

    @Override
    public String getColor(UUID uuid) {
        return "";
    }

    @Override
    public int getWeight(UUID uuid) {
        return 0;
    }
}
