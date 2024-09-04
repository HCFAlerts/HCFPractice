package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class NightCommand extends BaseCommand {

	@Command(name = "night")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.setPlayerTime(18000L, false);
		player.sendMessage(CC.GREEN + "It's now night time.");
	}
}
