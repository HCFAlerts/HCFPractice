package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitDeleteCommand extends BaseCommand {

	@Command(name = "kit.delete", permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /kit delete <kit>");
			return;
		}

		Kit kit = Kit.getByName(args[0]);
		if (kit == null) {
			player.sendMessage(CC.RED + "A kit with that name doesn't exists.");
			return;
		}

		kit.delete();
		player.sendMessage(CC.RED + "You deleted this kit.");
	}
}
