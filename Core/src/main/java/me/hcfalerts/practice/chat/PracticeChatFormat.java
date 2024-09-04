package me.hcfalerts.practice.chat;

import me.clip.placeholderapi.PlaceholderAPI;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.chat.impl.ChatFormat;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import org.bukkit.entity.Player;

public class PracticeChatFormat implements ChatFormat {
    @Override
    public String format(Player sender, Player receiver, String message) {
        Profile senderProfile = Profile.get(sender.getUniqueId());
        if (Clan.getByPlayer(sender) != null) {
            if (HCFPractice.get().isPlaceholderAPI())
                return PlaceholderAPI.setPlaceholders(sender, CC.translate(HCFPractice.get().getMainConfig().getString("CHAT.CLAN_FORMAT")
                        .replace("{prefix}", HCFPractice.get().getRankManager().getRank().getPrefix(sender.getUniqueId()))
                        .replace("{suffix}", HCFPractice.get().getRankManager().getRank().getSuffix(sender.getUniqueId()))
                        .replace("{color}", senderProfile.getColor())
                        .replace("{player}", sender.getName())
                        .replace("{message}", (sender.hasPermission("practice.chat.color") ? CC.translate(message) : CC.strip(message)))
                        .replace("{clan}", Clan.getByPlayer(sender).getColoredName())));
            return CC.translate(HCFPractice.get().getMainConfig().getString("CHAT.CLAN_FORMAT")
                    .replace("{prefix}", HCFPractice.get().getRankManager().getRank().getPrefix(sender.getUniqueId()))
                    .replace("{suffix}", HCFPractice.get().getRankManager().getRank().getSuffix(sender.getUniqueId()))
                    .replace("{color}", senderProfile.getColor())
                    .replace("{player}", sender.getName())
                    .replace("{message}", (sender.hasPermission("practice.chat.color") ? CC.translate(message) : CC.strip(message)))
                    .replace("{clan}", Clan.getByPlayer(sender).getColoredName()));
        }
        if (HCFPractice.get().isPlaceholderAPI())
            return PlaceholderAPI.setPlaceholders(sender, CC.translate(HCFPractice.get().getMainConfig().getString("CHAT.DEFAULT_FORMAT")
                    .replace("{prefix}", HCFPractice.get().getRankManager().getRank().getPrefix(sender.getUniqueId()))
                    .replace("{suffix}", HCFPractice.get().getRankManager().getRank().getSuffix(sender.getUniqueId()))
                    .replace("{color}", senderProfile.getColor())
                    .replace("{player}", sender.getName())
                    .replace("{message}", (sender.hasPermission("practice.chat.color") ? CC.translate(message) : CC.strip(message)))));
        return CC.translate(HCFPractice.get().getMainConfig().getString("CHAT.DEFAULT_FORMAT")
                .replace("{prefix}", HCFPractice.get().getRankManager().getRank().getPrefix(sender.getUniqueId()))
                .replace("{suffix}", HCFPractice.get().getRankManager().getRank().getSuffix(sender.getUniqueId()))
                .replace("{color}", senderProfile.getColor())
                .replace("{player}", sender.getName())
                .replace("{message}", (sender.hasPermission("practice.chat.color") ? CC.translate(message) : CC.strip(message))));
    }
}