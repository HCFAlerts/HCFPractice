package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SudoAllCommand extends BaseCommand {

	@Command(name = "sudoall", permission = "practice.command.sudoall")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage /sudoall <message>");
			return;
		}

		boolean noMe = false;
		StringBuilder message = new StringBuilder();

		if (args[args.length - 1].equals("-s")) {
			args[args.length - 1] = null;
			noMe = true;
		}
		for (String arg : args) {
			message.append(arg).append(" ");
		}

		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (noMe && onlinePlayer.equals(player)) continue;
			onlinePlayer.chat(CC.translate(message.toString()));
		}

		player.sendMessage(ChatColor.GREEN + "Forced all players to chat!");
	}
}
