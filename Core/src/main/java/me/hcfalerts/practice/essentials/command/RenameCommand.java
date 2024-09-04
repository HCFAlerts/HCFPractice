package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RenameCommand extends BaseCommand {

	@Command(name = "rename", permission = "practice.command.rename")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please insert a valid name.");
			return;
		}

		if (player.getItemInHand() != null) {
			ItemStack itemStack = player.getItemInHand();
			ItemMeta itemMeta = itemStack.getItemMeta();
			StringBuilder string = new StringBuilder();
			for (String message : args) {
				string.append(message).append(" ");
			}
			itemMeta.setDisplayName(CC.translate(string.toString()));
			itemStack.setItemMeta(itemMeta);

			player.updateInventory();
			player.sendMessage(CC.GREEN + "You renamed the item in your hand.");
		} else {
			player.sendMessage(CC.RED + "There is nothing in your hand.");
		}
	}
}
