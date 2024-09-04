package me.hcfalerts.practice.tablist.impl.utils.ping.impl;

import me.hcfalerts.practice.tablist.impl.utils.ping.IPingProvider;
import org.bukkit.entity.Player;

public class LunarPingImpl implements IPingProvider {

    @Override
    public int getDefaultPing(Player player) {
        return 0;
    }

}
