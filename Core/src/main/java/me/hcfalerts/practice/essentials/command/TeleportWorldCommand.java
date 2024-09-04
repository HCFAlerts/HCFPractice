package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TeleportWorldCommand extends BaseCommand {

	@Command(name = "tpworld", aliases = {"world", "changeworld"}, permission = "practice.command.tpworld")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /tpworld <world>");
			return;
		}

		World world = Bukkit.getWorld(args[0]);
		if (world == null) {
			player.sendMessage(CC.RED + "A world with that name does not exist.");
		} else {
			player.teleport(world.getSpawnLocation());
			player.sendMessage(CC.GREEN + "Teleported you to " + world.getName());
		}
	}
}
