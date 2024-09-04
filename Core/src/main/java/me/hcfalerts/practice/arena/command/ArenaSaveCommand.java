package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaSaveCommand extends BaseCommand {

	@Command(name = "arena.save", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		for (Arena arena : Arena.getArenas()) {
			arena.save();
		}
		player.sendMessage(ChatColor.GREEN + "Saved all arenas!");
	}

}
