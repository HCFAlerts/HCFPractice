package me.hcfalerts.practice.chat.impl.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.chat.impl.Chat;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteChatCommand extends BaseCommand {

	@Command(name = "mutechat", permission = "practice.staff.mutechat", inGameOnly = false)
	@Override
	public void onCommand(CommandArgs commandArgs) {
		CommandSender sender = commandArgs.getSender();

		Chat.togglePublicChatMute();

		String senderName;

		if (sender instanceof Player) {
			senderName = Profile.get(((Player) sender).getUniqueId()).getColor() + sender.getName();
		} else {
			senderName = ChatColor.DARK_RED + "Console";
		}

		String context = Chat.isPublicChatMuted() ? "muted" : "unmuted";

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.get(onlinePlayer.getUniqueId());
			new MessageFormat(Locale.MUTE_CHAT_BROADCAST.format(profile.getLocale()))
					.add("{sender_name}", senderName)
					.add("{context}", context)
					.send(onlinePlayer);
		}
	}
}
