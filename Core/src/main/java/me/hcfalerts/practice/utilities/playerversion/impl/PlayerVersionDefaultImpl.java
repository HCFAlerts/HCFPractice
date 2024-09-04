package me.hcfalerts.practice.utilities.playerversion.impl;

import me.hcfalerts.practice.utilities.playerversion.IPlayerVersion;
import me.hcfalerts.practice.utilities.playerversion.PlayerVersion;
import org.bukkit.entity.Player;

public class PlayerVersionDefaultImpl implements IPlayerVersion {
    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(0);
    }
}
