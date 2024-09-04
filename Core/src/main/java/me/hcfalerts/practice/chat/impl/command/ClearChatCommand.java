package me.hcfalerts.practice.chat.impl.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClearChatCommand extends BaseCommand {

	@Command(name = "clearchat", aliases = {"cc"}, permission = "practice.staff.clearchat", inGameOnly = false)
	@Override
	public void onCommand(CommandArgs commandArgs) {
		CommandSender sender = commandArgs.getSender();

		String[] strings = new String[101];

		for (Player player : Bukkit.getOnlinePlayers()) {
			if (!player.hasPermission("practice.staff")) {
				player.sendMessage(strings);
			}
		}

		String senderName;

		if (sender instanceof Player) {
			Profile profile = Profile.get(((Player) sender).getUniqueId());
			senderName = profile.getColor() + sender.getName();
		} else {
			senderName = ChatColor.DARK_RED + "Console";
		}

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			Profile profile = Profile.get(onlinePlayer.getUniqueId());
			new MessageFormat(Locale.CLEAR_CHAT_BROADCAST.format(profile.getLocale()))
					.add("{sender_name}", senderName)
					.send(onlinePlayer);
		}
	}
}
