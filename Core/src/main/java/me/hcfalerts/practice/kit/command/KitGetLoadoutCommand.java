package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitGetLoadoutCommand extends BaseCommand {

	@Command(name = "kit.getloadout", aliases = {"kit.getinv", "kit.getinventory"}, permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /kit getloadout <kit>");
			return;
		}

		Kit kit = Kit.getByName(args[0]);
		if (kit == null) {
			player.sendMessage(CC.RED + "A kit with that name does not exist.");
			return;
		}

		player.getInventory().setArmorContents(kit.getKitLoadout().getArmor());
		player.getInventory().setContents(kit.getKitLoadout().getContents());
		player.updateInventory();

		player.sendMessage(CC.GREEN + "You received the kit's loadout.");
	}
}
