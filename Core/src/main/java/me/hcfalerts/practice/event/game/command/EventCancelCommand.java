package me.hcfalerts.practice.event.game.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EventCancelCommand extends BaseCommand {

	@Command(name = "event.cancel", permission = "practice.event.admin", inGameOnly = false)
	@Override
	public void onCommand(CommandArgs commandArgs) {
		CommandSender sender = commandArgs.getSender();

		if (EventGame.getActiveGame() != null) {
			EventGame.getActiveGame().getGameLogic().cancelEvent();
		} else {
			sender.sendMessage(ChatColor.RED + "There is no active event.");
		}
	}
}
