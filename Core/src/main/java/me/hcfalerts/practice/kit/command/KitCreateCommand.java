package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitCreateCommand extends BaseCommand {

	@Command(name = "kit.create", permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /kit create <kit>");
			return;
		}

		String kitName = args[0];
		if (Kit.getByName(kitName) != null) {
			player.sendMessage(CC.RED + "A kit with that name already exists.");
			return;
		}

		Kit kit = new Kit(kitName);
		kit.save();
		Kit.getKits().add(kit);
		Queue.getQueues().add(new Queue(kit, false));

		player.sendMessage(CC.GREEN + "You created a new kit.");
	}
}
