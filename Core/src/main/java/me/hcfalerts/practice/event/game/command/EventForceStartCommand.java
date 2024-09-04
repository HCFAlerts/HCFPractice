package me.hcfalerts.practice.event.game.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameState;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EventForceStartCommand extends BaseCommand {

	@Command(name = "event.forcestart", permission = "practice.event.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		if (EventGame.getActiveGame() != null) {
			EventGame game = EventGame.getActiveGame();

			if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
					game.getGameState() == EventGameState.STARTING_EVENT) {
				game.getGameLogic().startEvent();
				game.getGameLogic().preStartRound();
				game.setGameState(EventGameState.STARTING_ROUND);
				game.getGameLogic().getGameLogicTask().setNextAction(4);
			} else {
				player.sendMessage(ChatColor.RED + "The event has already started.");
			}
		} else {
			player.sendMessage(ChatColor.RED + "There is no active event.");
		}
	}
}
