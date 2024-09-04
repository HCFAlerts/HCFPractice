package me.hcfalerts.practice.event.game.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.utilities.Cooldown;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class EventClearCooldownCommand extends BaseCommand {

	@Command(name = "event.clearcooldown", aliases = {"event.clearcd"}, permission = "practice.event.admin", inGameOnly = false)
	@Override
	public void onCommand(CommandArgs commandArgs) {
		CommandSender sender = commandArgs.getSender();

		EventGame.setCooldown(new Cooldown(0));
		sender.sendMessage(ChatColor.GREEN + "You cleared the event cooldown.");
	}
}
