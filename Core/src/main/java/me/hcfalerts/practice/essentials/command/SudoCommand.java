package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SudoCommand extends BaseCommand {

	@Command(name = "sudo", permission = "practice.command.sudo")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0 || args.length == 1) {
			player.sendMessage(CC.RED + "Please usage: /sudo <player> <message>");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			new MessageFormat(Locale.PLAYER_NOT_FOUND
					.format(Profile.get(player.getUniqueId()).getLocale()))
					.send(player);
			return;
		}

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			stringBuilder.append(args[i]).append(" ");
		}

		target.chat(CC.translate(stringBuilder.toString()));
		player.sendMessage(ChatColor.GREEN + "Forced target to chat!");
	}
}
