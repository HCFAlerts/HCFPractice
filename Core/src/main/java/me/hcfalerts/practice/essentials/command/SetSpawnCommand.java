package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class SetSpawnCommand extends BaseCommand {

	@Command(name = "setspawn", permission = "practice.command.setspawn")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		HCFPractice.get().getEssentials().setSpawnAndSave(player.getLocation());
		player.sendMessage(CC.GREEN + "You updated the spawn.");
	}
}
