package me.hcfalerts.practice.event.game.map.vote.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.Cooldown;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class EventMapVoteCommand extends BaseCommand {

	@Command(name = "eventvote")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /eventvote (mapName)");
			return;
		}

		EventGameMap gameMap = EventGameMap.getByName(args[0]);
		if (gameMap == null) {
			player.sendMessage(ChatColor.RED + "You cannot vote for a map that doesn't exist!");
			return;
		}

		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getState() == ProfileState.EVENT && EventGame.getActiveGame() != null) {
			if (profile.getVoteCooldown().hasExpired()) {
				profile.setVoteCooldown(new Cooldown(5000));
				EventGame.getActiveGame().getGameLogic().onVote(player, gameMap);
			} else {
				player.sendMessage(ChatColor.RED + "You can vote in another " +
						profile.getVoteCooldown().getTimeLeft() + ".");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You are not in an event.");
		}
	}
}
