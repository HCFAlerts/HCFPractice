package me.hcfalerts.practice.tablist.impl;

import me.hcfalerts.practice.tablist.impl.utils.BufferedTabObject;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public interface GhostlyAdapter {

    Set<BufferedTabObject> getSlots(Player player);

    List<String> getFooter(Player player);
    List<String> getHeader(Player player);

}
