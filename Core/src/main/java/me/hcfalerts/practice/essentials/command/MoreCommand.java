package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class MoreCommand extends BaseCommand {

	@Command(name = "more", permission = "practice.command.more")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		if (player.getItemInHand() == null) {
			player.sendMessage(CC.RED + "There is nothing in your hand.");
			return;
		}

		player.getItemInHand().setAmount(64);
		player.updateInventory();
		player.sendMessage(CC.GREEN + "You gave yourself more of the item in your hand.");
	}
}
