package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaRemoveKitCommand extends BaseCommand {

	@Command(name = "arena.removekit", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		Arena arena = Arena.getByName(args[0]);
		if (arena == null) {
			player.sendMessage(ChatColor.RED + "An arena with that name does not exist.");
			return;
		}

		Kit kit = Kit.getByName(args[1]);
		if (kit == null) {
			player.sendMessage(ChatColor.RED + "A kit with that name does not exist.");
			return;
		}

		arena.getKits().remove(kit.getName());
		arena.save();

		player.sendMessage(ChatColor.RED + "Removed kit \"" + kit.getName() +
				"\" from arena \"" + arena.getName() + "\"");
	}
}