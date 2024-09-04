package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.utilities.BukkitReflection;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class SetSlotsCommand extends BaseCommand {

	@Command(name = "setslots", permission = "practice.command.setslots")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please insert a valid slot.");
			return;
		}

		int slots;
		if (!StringUtils.isNumeric(args[0])) {
			player.sendMessage(CC.RED + "Please insert a valid integer");
			return;
		}
		slots = Integer.getInteger(args[0]);

		BukkitReflection.setMaxPlayers(HCFPractice.get().getServer(), slots);
		player.sendMessage(CC.GREEN + "You set the max slots to " + slots + ".");
	}
}
