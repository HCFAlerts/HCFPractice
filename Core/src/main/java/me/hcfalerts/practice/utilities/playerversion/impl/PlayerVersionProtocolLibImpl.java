package me.hcfalerts.practice.utilities.playerversion.impl;

import com.comphenix.protocol.ProtocolLibrary;
import me.hcfalerts.practice.utilities.playerversion.IPlayerVersion;
import me.hcfalerts.practice.utilities.playerversion.PlayerVersion;
import org.bukkit.entity.Player;

public class PlayerVersionProtocolLibImpl implements IPlayerVersion {

    @Override
    public PlayerVersion getPlayerVersion(Player player) {
        return PlayerVersion.getVersionFromRaw(
                ProtocolLibrary.getProtocolManager().getProtocolVersion(player)
        );
    }
}
