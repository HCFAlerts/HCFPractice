package me.hcfalerts.practice.match.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpectateCommand extends BaseCommand {

	@Command(name = "spectate", aliases = {"spec"})
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /spectate (player)");
			return;
		}

		Profile playerProfile = Profile.get(player.getUniqueId());
		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			new MessageFormat(Locale.PLAYER_NOT_FOUND
					.format(playerProfile.getLocale()))
					.send(player);
			return;
		}

		if (playerProfile.isBusy()) {
			player.sendMessage(CC.RED + "You must be in the lobby and not queueing to spectate.");
			return;
		}

		if (playerProfile.getParty() != null) {
			player.sendMessage(CC.RED + "You must leave your party to spectate a match.");
			return;
		}

		Profile targetProfile = Profile.get(target.getUniqueId());

		if (targetProfile.getState() != ProfileState.FIGHTING) {
			player.sendMessage(CC.RED + "That player is not in a match.");
			return;
		}

		if (!targetProfile.getOptions().allowSpectators()) {
			player.sendMessage(CC.RED + "That player is not allowing spectators.");
			return;
		}

		TaskUtil.run(() -> targetProfile.getMatch().addSpectator(player, target, false));
	}
}
