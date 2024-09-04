package me.hcfalerts.practice.event.game.map.command;

import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.event.game.map.impl.SpreadEventGameMap;
import me.hcfalerts.practice.event.game.map.impl.TeamEventGameMap;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.entity.Player;

public class EventMapStatusCommand extends BaseCommand {

	@Command(name = "event.map.status", permission = "practice.event.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /event map status (mapName)");
			return;
		}

		EventGameMap gameMap = EventGameMap.getByName(args[0]);
		if (gameMap == null) {
			player.sendMessage(CC.RED + "An event map with that name does not exist.");
		} else {
			player.sendMessage(CC.PINK + CC.BOLD + "Event Map Status " + CC.GRAY + "(" +
					(gameMap.isSetup() ? CC.GREEN : CC.RED) + gameMap.getMapName() + CC.GRAY + ")");
			player.sendMessage(CC.GREEN + "Spectator Location: " + CC.WHITE +
					(gameMap.getSpectatorPoint() == null ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));

			if (gameMap instanceof TeamEventGameMap) {
				TeamEventGameMap teamGameMap = (TeamEventGameMap) gameMap;

				player.sendMessage(CC.GREEN + "Spawn A Location: " + CC.WHITE +
						(teamGameMap.getSpawnPointA() == null ?
								StringEscapeUtils.unescapeJava("\u2717") :
								StringEscapeUtils.unescapeJava("\u2713")));

				player.sendMessage(CC.GREEN + "Spawn B Location: " + CC.WHITE +
						(teamGameMap.getSpawnPointB() == null ?
								StringEscapeUtils.unescapeJava("\u2717") :
								StringEscapeUtils.unescapeJava("\u2713")));
			} else if (gameMap instanceof SpreadEventGameMap) {
				SpreadEventGameMap spreadGameMap = (SpreadEventGameMap) gameMap;

				player.sendMessage(CC.GREEN + "Spread Locations: " + CC.WHITE +
						(spreadGameMap.getSpawnLocations().isEmpty() ?
								StringEscapeUtils.unescapeJava("\u2717") :
								StringEscapeUtils.unescapeJava("\u2713")));
			}
		}
	}
}
