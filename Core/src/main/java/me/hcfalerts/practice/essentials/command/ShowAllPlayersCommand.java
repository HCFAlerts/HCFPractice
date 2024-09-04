package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ShowAllPlayersCommand extends BaseCommand {

	@Command(name = "showallplayers", permission = "practice.command.showallplayers")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
			player.showPlayer(otherPlayer);
		}
	}
}
