package me.hcfalerts.practice.chat.impl.format;

import me.hcfalerts.practice.chat.impl.ChatFormat;
import me.hcfalerts.practice.utilities.chat.CC;
import org.bukkit.entity.Player;

public class DefaultChatFormat implements ChatFormat {

    @Override
    public String format(Player sender, Player receiver, String message) {
        return CC.translate(sender.getDisplayName() + "&7:&f " +
            (sender.hasPermission("practice.chat.color") ? CC.translate(message) : CC.strip(message)));
    }

}
