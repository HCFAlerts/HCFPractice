package me.hcfalerts.practice.utilities.rank.impl;

import me.hcfalerts.practice.utilities.rank.IRank;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.util.UUID;

public class PermissionsEx implements IRank {

    private Chat chat;

    public PermissionsEx() {
        RegisteredServiceProvider<Chat> rsp = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (rsp != null) chat = rsp.getProvider();
    }

    @Override
    public String getRankSystem() {
        return "PermissionsEx";
    }

    @Override
    public String getName(UUID uuid) {
        return chat.getPrimaryGroup(getPlayer(uuid));
    }

    @Override
    public String getPrefix(UUID uuid) {
        return chat.getPlayerPrefix(getPlayer(uuid));
    }

    @Override
    public String getSuffix(UUID uuid) {
        return chat.getPlayerSuffix(getPlayer(uuid));
    }

    @Override
    public String getColor(UUID uuid) {
        return "";
    }

    @Override
    public int getWeight(UUID uuid) {
        return 0;
    }

    private Player getPlayer(UUID uuid) {
        return Bukkit.getPlayer(uuid);
    }
}
