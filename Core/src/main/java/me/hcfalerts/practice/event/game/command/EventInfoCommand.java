package me.hcfalerts.practice.event.game.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.impl.sumo.SumoEvent;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventInfoCommand extends BaseCommand {

	@Command(name = "event.info")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		if (EventGame.getActiveGame() == null) {
			player.sendMessage(CC.RED + "There is no active event.");
			return;
		}

		EventGame game = EventGame.getActiveGame();

		player.sendMessage(CC.PINK + CC.BOLD + "Event Information");
		player.sendMessage(CC.PINK + "State" + CC.GRAY + ": " + CC.WHITE + game.getGameState().getReadable());
		player.sendMessage(CC.PINK + "Players" + CC.GRAY + ": " + CC.WHITE + game.getRemainingPlayers() +
				"/" + game.getMaximumPlayers());

		if (game.getEvent() instanceof SumoEvent) {
			player.sendMessage(CC.PINK + "Round" + CC.GRAY + ": " + CC.WHITE + game.getGameLogic().getRoundNumber());
		}
	}
}
