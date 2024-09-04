package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class SunsetCommand extends BaseCommand {

	@Command(name = "sunset")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.setPlayerTime(12000, false);
		player.sendMessage(CC.GREEN + "It's now sunset.");
	}
}
