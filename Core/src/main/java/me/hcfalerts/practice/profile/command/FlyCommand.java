package me.hcfalerts.practice.profile.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class FlyCommand extends BaseCommand {

	@Command(name = "fly", permission = "practice.command.fly")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
			if (player.getAllowFlight()) {
				player.setAllowFlight(false);
				player.setFlying(false);
				player.updateInventory();
				new MessageFormat(Locale.ESSENTIALS_FLY_COMMAND_DEACTIVATE
						.format(profile.getLocale()))
						.send(player);
			} else {
				player.setAllowFlight(true);
				player.setFlying(true);
				player.updateInventory();
				new MessageFormat(Locale.ESSENTIALS_FLY_COMMAND_ACTIVATE
						.format(profile.getLocale()))
						.send(player);
			}
		} else {
			new MessageFormat(Locale.ESSENTIALS_FLY_COMMAND_CANNOT_FLY
					.format(profile.getLocale()))
					.send(player);
		}
	}
}
