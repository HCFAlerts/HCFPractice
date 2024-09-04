package me.hcfalerts.practice.duel.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.meta.ProfileRematchData;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class RematchCommand extends BaseCommand {

	@Command(name = "rematch")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		Profile profile = Profile.get(player.getUniqueId());
		ProfileRematchData rematchData = profile.getRematchData();

		if (rematchData == null) {
			new MessageFormat(Locale.REMATCH_DO_NOT_HAVE_ANYONE.format(profile.getLocale()))
					.send(player);
			return;
		}

		rematchData.validate();

		if (rematchData.isCancelled()) {
			new MessageFormat(Locale.REMATCH_CANCELLED.format(profile.getLocale()))
					.send(player);
			return;
		}

		if (rematchData.isReceive()) {
			rematchData.accept();
		} else {
			if (rematchData.isSent()) {
				new MessageFormat(Locale.REMATCH_IS_SENT.format(profile.getLocale()))
						.send(player);
				return;
			}

			rematchData.request();
		}
	}
}
