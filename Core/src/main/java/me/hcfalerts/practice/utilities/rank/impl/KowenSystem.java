package me.hcfalerts.practice.utilities.rank.impl;

import me.activated.core.api.player.GlobalPlayer;
import me.activated.core.api.player.PlayerData;
import me.activated.core.plugin.AquaCoreAPI;
import me.hcfalerts.practice.utilities.rank.IRank;

import java.util.UUID;

public class KowenSystem implements IRank {
    @Override
    public String getRankSystem() {
        return "KowenSystem";
    }

    @Override
    public String getName(UUID uuid) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(uuid);
        return data == null ? "No Data" : data.getHighestRank().getName();
    }

    @Override
    public String getPrefix(UUID uuid) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(uuid);
        return data == null ? "No Data" : data.getHighestRank().getPrefix();
    }

    @Override
    public String getSuffix(UUID uuid) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(uuid);
        return data == null ? "No Data" : data.getHighestRank().getSuffix();
    }

    @Override
    public String getColor(UUID uuid) {
        PlayerData data = AquaCoreAPI.INSTANCE.getPlayerData(uuid);
        return data == null ? "No Data" : data.getHighestRank().getColor() + "";
    }

    @Override
    public int getWeight(UUID uuid) {
        GlobalPlayer data = AquaCoreAPI.INSTANCE.getGlobalPlayer(uuid);
        return data == null ? 0 : data.getRankWeight();
    }
}
