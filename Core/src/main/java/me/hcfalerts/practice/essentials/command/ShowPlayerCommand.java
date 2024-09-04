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

public class ShowPlayerCommand extends BaseCommand {

	@Command(name = "showplayer", permission = "practice.command.showplayer")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /showplayer (player) or /showplayer (player) (target)");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (args.length == 1) {
			if (target == null) {
				new MessageFormat(Locale.PLAYER_NOT_FOUND
						.format(Profile.get(player.getUniqueId()).getLocale()))
						.send(player);
				return;
			}
			player.showPlayer(target);
			player.sendMessage(ChatColor.GREEN + "Showing you " + target.getName());
		}
		else {
			Player target2 = Bukkit.getPlayer(args[1]);
			if (target2 == null) {
				new MessageFormat(Locale.PLAYER_NOT_FOUND
						.format(Profile.get(player.getUniqueId()).getLocale()))
						.send(player);
				return;
			}
			target.showPlayer(target2);
			player.sendMessage(ChatColor.GREEN + "Showing " + target2.getName() + " to " + target.getName());
		}
	}
}
