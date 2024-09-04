package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class HealCommand extends BaseCommand {

	@Command(name = "heal", permission = "practice.command.heal")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.setHealth(20.0);
			player.setFoodLevel(20);
			player.setSaturation(5.0F);
			player.updateInventory();
			player.sendMessage(CC.GREEN + "You healed yourself.");
		} else {
			Player target = commandArgs.getPlayer();
			if (target == null) {
				new MessageFormat(Locale.PLAYER_NOT_FOUND
						.format(Profile.get(player.getUniqueId()).getLocale()))
						.send(player);
				return;
			}
			target.setHealth(20.0);
			target.setFoodLevel(20);
			target.setSaturation(5.0F);
			target.updateInventory();
			target.sendMessage(CC.GREEN + "You have been healed by " + player.getName());
		}
	}
}
