package me.hcfalerts.practice.event.game.map.command;

import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventMapsCommand extends BaseCommand {

	@Command(name = "event.maps", permission = "practice.event.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.sendMessage(CC.PINK + CC.BOLD + "Event Maps");

		if (EventGameMap.getMaps().isEmpty()) {
			player.sendMessage(CC.GRAY + "There are no event maps.");
		} else {
			for (EventGameMap gameMap : EventGameMap.getMaps()) {
				player.sendMessage(" - " + (gameMap.isSetup() ? CC.GREEN : CC.RED) + gameMap.getMapName());
			}
		}
	}
}
