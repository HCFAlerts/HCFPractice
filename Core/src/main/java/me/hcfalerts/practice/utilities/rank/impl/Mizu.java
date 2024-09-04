package me.hcfalerts.practice.utilities.rank.impl;

import com.broustudio.MizuAPI.MizuAPI;
import me.hcfalerts.practice.utilities.rank.IRank;
import me.hcfalerts.practice.utilities.rank.RankObject;

import java.util.UUID;

public class Mizu implements IRank, RankObject {

    @Override
    public String getRankSystem() {
        return "MizuCore";
    }

    @Override
    public String getName(UUID uuid) {
        return getRank(uuid);
    }

    @Override
    public String getPrefix(UUID uuid) {
        return MizuAPI.getAPI().getRankPrefix(getRank(uuid));
    }

    @Override
    public String getSuffix(UUID uuid) {
        return MizuAPI.getAPI().getRankSuffix(getRank(uuid));
    }

    @Override
    public String getColor(UUID uuid) {
        return MizuAPI.getAPI().getRankColor(getRank(uuid));
    }

    @Override
    public int getWeight(UUID uuid) {
        return 0;
    }

    @Override
    public String getRank(UUID uuid) {
        return MizuAPI.getAPI().getRank(uuid);
    }
}
