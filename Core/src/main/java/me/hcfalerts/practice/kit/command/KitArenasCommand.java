package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitArenasCommand extends BaseCommand {

	@Command(name = "kit.arenas", permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /kit arenas <kit>");
			return;
		}

		Kit kit = Kit.getByName(args[0]);
		if (kit == null) {
			player.sendMessage(CC.RED + "A kit with that name does not exist.");
			return;
		}
		player.sendMessage(CC.CHAT_BAR);
		player.sendMessage(CC.translate("&d&lArenas with this Kit &7[&f" + kit.getName() + "&7]"));
		player.sendMessage("");
		for (Arena arena : Arena.getArenas()) {
			if (arena.getKits().contains(kit.getName())) {
				player.sendMessage(CC.translate("&7» &f" + arena.getName()));
			}
		}
		player.sendMessage(CC.CHAT_BAR);
	}
}
