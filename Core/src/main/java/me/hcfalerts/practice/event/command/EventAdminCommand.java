package me.hcfalerts.practice.event.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventAdminCommand extends BaseCommand {

	private final String[][] HELP = new String[][] {
			new String[]{ "/event forcestart", "Force start the active event" },
			new String[]{ "/event cancel", "Cancel the active event" },
			new String[]{ "/event clearcd", "Clear the event cooldown" },
			new String[]{ "/event setlobby <event>", "Set lobby location" },
			new String[]{ "/event addmap <event> <map>", "Allow a map to be played" },
			new String[]{ "/event removemap <event> <map>", "Deny a map to be played" },
			new String[]{ "/event map create <name> <spread|team>", "Create a map" },
			new String[]{ "/event map delete <name>", "Delete a map" },
			new String[]{ "/event map setspawn <name> <spread|a|b|spectator|", "Set a spawn point" },
			new String[]{ "/event map status <map>", "Check the status of a map" },
			new String[]{ "/event maps", "View all maps" }
	};

	@Command(name = "event.admin", permission = "practice.event.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.sendMessage(CC.CHAT_BAR);
		player.sendMessage(CC.PINK + CC.BOLD + "Event Admin");

		for (String[] command : HELP) {
			player.sendMessage(CC.PINK + command[0] + CC.GRAY + " - " + CC.WHITE + command[1]);
		}

		player.sendMessage(CC.CHAT_BAR);
	}
}
