package me.hcfalerts.practice.utilities.rank.impl;


import me.hcfalerts.practice.utilities.rank.IRank;

import java.util.UUID;

public class Default implements IRank {

    @Override
    public String getRankSystem() {
        return "Default";
    }

    @Override
    public String getName(UUID uuid) {
        return "";
    }

    @Override
    public String getPrefix(UUID uuid) {
        return "";
    }

    @Override
    public String getSuffix(UUID uuid) {
        return "";
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
