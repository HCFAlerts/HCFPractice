package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ArenaDeleteCommand extends BaseCommand {

	@Command(name = "arena.delete", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		Arena arena = Arena.getByName(args[0]);
		if (arena != null) {
			arena.delete();

			player.sendMessage(CC.RED + "Deleted arena \"" + arena.getName() + "\"");
		} else {
			player.sendMessage(CC.RED + "An arena with that name does not exist.");
		}
	}

}
