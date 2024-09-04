package me.hcfalerts.practice.event.game.map.command;

import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventMapDeleteCommand extends BaseCommand {

	@Command(name = "event.map.delete", permission = "practice.event.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /event map delete (mapName)");
			return;
		}

		EventGameMap gameMap = EventGameMap.getByName(args[0]);
		if (gameMap == null) {
			player.sendMessage(CC.RED + "An event map with that name already exists.");
			return;
		}

		gameMap.delete();
		EventGameMap.getMaps().remove(gameMap);
		player.sendMessage(CC.GREEN + "You successfully deleted the event map \"" + gameMap.getMapName() + "\".");
	}
}
