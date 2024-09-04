package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClearCommand extends BaseCommand {

	@Command(name = "clearinv", aliases = {"clear", "ci"}, permission = "practice.command.clearinv")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.getInventory().setContents(new ItemStack[36]);
			player.getInventory().setArmorContents(new ItemStack[4]);
			player.updateInventory();
			player.sendMessage(CC.GREEN + "You cleared your inventory.");
		}
		else {
		    Player target = Bukkit.getPlayer(args[0]);
		    if (target == null) {
				new MessageFormat(Locale.PLAYER_NOT_FOUND
						.format(Profile.get(player.getUniqueId()).getLocale()))
						.send(player);
		        return;
            }
            target.getInventory().setContents(new ItemStack[36]);
            target.getInventory().setArmorContents(new ItemStack[4]);
            target.updateInventory();
            target.sendMessage(CC.GREEN + "Your inventory has been cleared by " + player.getName());
        }
	}
}
