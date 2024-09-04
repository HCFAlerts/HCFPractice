package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class KitSetRankedSlotCommand extends BaseCommand {

	@Command(name = "kit.setrankedslot", permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /kit setrankedslot <kit> <slot>");
			return;
		}

		Kit kit = Kit.getByName(args[0]);
		if (kit == null) {
			player.sendMessage(CC.RED + "A kit with that name does not exist.");
			return;
		}

		int slot;
		if (!StringUtils.isNumeric(args[1])) {
			player.sendMessage(CC.RED + "Please usage a valid slot.");
			return;
		}
		slot = Integer.parseInt(args[1]);

		if (slot > 44) {
			player.sendMessage(CC.RED + "You have exceeded the maximum");
			return;
		}

		kit.setRankedSlot(slot);
		player.sendMessage(CC.GREEN + "Ranked Slot changed to " + kit.getName() + " successfully to " + slot);
	}
}
