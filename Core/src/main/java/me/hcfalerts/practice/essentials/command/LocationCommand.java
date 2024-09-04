package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.LocationUtil;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class LocationCommand extends BaseCommand {

	@Command(name = "location", aliases = {"loc"}, permission = "practice.command.loc")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.sendMessage(LocationUtil.serialize(player.getLocation()));
	}
}
