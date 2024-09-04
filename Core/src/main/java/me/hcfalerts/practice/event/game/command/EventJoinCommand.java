package me.hcfalerts.practice.event.game.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameState;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventJoinCommand extends BaseCommand {

	@Command(name = "event.join")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getParty() != null) {
			player.sendMessage(CC.RED + "You cannot join the event while in a party.");
			return;
		}

		if (profile.isBusy()) {
			player.sendMessage(CC.RED + "You must be in the lobby to join the event.");
		} else {
			EventGame game = EventGame.getActiveGame();

			if (game != null) {
				if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
						game.getGameState() == EventGameState.STARTING_EVENT) {
					if (game.getParticipants().size() < game.getMaximumPlayers()) {
						game.getGameLogic().onJoin(player);
					} else {
						player.sendMessage(CC.RED + "The event is full.");
					}
				} else {
					player.sendMessage(CC.RED + "The event has already started.");
				}
			} else {
				player.sendMessage(CC.RED + "There is no active event.");
			}
		}
	}
}
