package me.hcfalerts.practice.event.game.map.command;

import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.event.game.map.impl.SpreadEventGameMap;
import me.hcfalerts.practice.event.game.map.impl.TeamEventGameMap;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventMapSetSpawnCommand extends BaseCommand {

	@Command(name = "event.map.setspawn", aliases = {"addspawn"}, permission = "practice.event.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0 || args.length == 1) {
			player.sendMessage(CC.RED + "Please usage: /event map setspawn (mapName) (a|b|spectator|spread)");
			return;
		}

		EventGameMap map = EventGameMap.getByName(args[0]);
		String field = args[1];
		if (map == null) {
			player.sendMessage(CC.RED + "An event map with that name does not exist.");
		} else {
			switch (field.toLowerCase()) {
				case "spectator": {
					map.setSpectatorPoint(player.getLocation());

					player.sendMessage(CC.GREEN + "You successfully updated " +
							map.getMapName() + "'s " + field + " location.");
				}
				break;
				case "a": {
					if (!(map instanceof TeamEventGameMap)) {
						player.sendMessage(CC.RED + "That type of map only has spread locations!");
						player.sendMessage(CC.RED + "To add a location to the spread list, use " +
								"/event map set <map> spread.");
						break;
					}

					TeamEventGameMap teamMap = (TeamEventGameMap) map;
					teamMap.setSpawnPointA(player.getLocation());

					player.sendMessage(CC.GREEN + "You successfully updated " +
							map.getMapName() + "'s " + field + " location.");
				}
				break;
				case "b": {
					if (!(map instanceof TeamEventGameMap)) {
						player.sendMessage(CC.RED + "That type of map only has spread locations!");
						player.sendMessage(CC.RED + "To add a location to the spread list, use " +
								"/event map set <map> spread.");
						break;
					}

					TeamEventGameMap teamMap = (TeamEventGameMap) map;
					teamMap.setSpawnPointB(player.getLocation());

					player.sendMessage(CC.GREEN + "You successfully updated " +
							map.getMapName() + "'s " + field + " location.");
				}
				break;
				case "spread": {
					if (!(map instanceof SpreadEventGameMap)) {
						player.sendMessage(CC.RED + "That type of map does not have spread locations!");
						player.sendMessage(CC.RED + "To set one of the locations, use " +
								"/event map set <map> <a/b>.");
						break;
					}

					SpreadEventGameMap spreadMap = (SpreadEventGameMap) map;
					spreadMap.getSpawnLocations().add(player.getLocation());

					player.sendMessage(CC.GREEN + "You successfully added a location to " +
							map.getMapName() + "'s " + field + " list.");
				}
				break;
				default:
					player.sendMessage(CC.RED + "A field by that name does not exist.");
					player.sendMessage(CC.RED + "Fields: spectator, a, b");
					return;
			}

			map.save();
		}
	}
}
