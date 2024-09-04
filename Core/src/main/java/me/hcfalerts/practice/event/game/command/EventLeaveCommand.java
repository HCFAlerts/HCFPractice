package me.hcfalerts.practice.event.game.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventLeaveCommand extends BaseCommand {

	@Command(name = "event.leave")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getState() == ProfileState.EVENT) {
			if (EventGame.getActiveGame().getGameHost().getUsername().equals(player.getName())) {
				player.sendMessage(CC.translate("&cYou cannot leave the event since you are the one who hosts."));
				return;
			}
			EventGame.getActiveGame().getGameLogic().onLeave(player);
		} else {
			player.sendMessage(CC.RED + "You are not in an event.");
		}
	}
}
