package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.Cooldown;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class TimerCommand extends BaseCommand {

	@Command(name = "timer", permission = "practice.command.timer")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			for (String s : Arrays.asList(
					"&7&m------------------------------------------------",
					"&d&lTimer Command",
					"",
					"&f<Needed> &7| &f[Optional]",
					"",
					"&dTimers&7: &fenderpearl, event",
					"",
					"&f/timer clear <timer> [player]",
			        "------------------------------------------------")) {
				player.sendMessage(CC.translate(s));
			}
		}
		else if (args[0].equalsIgnoreCase("clear")) {
			if (args.length == 1) {
				player.sendMessage(CC.translate("&dTimers: &fenderpearl, event"));
			}
			else if (args[1].equalsIgnoreCase("enderpearl")) {
				if (args.length == 3 && Bukkit.getPlayer(args[2]) != null) {
					Player target = Bukkit.getPlayer(args[2]);
					Profile profile = Profile.get(target.getUniqueId());
					if (profile.getEnderpearlCooldown() != null) {
						profile.getEnderpearlCooldown().setForceExpired(true);
						player.sendMessage(CC.translate("&d" + target.getName() + " Enderpearl cooldown cleared!"));
					}
					return;
				}
				Profile profile = Profile.get(player.getUniqueId());
				if (profile.getEnderpearlCooldown() != null) {
					profile.getEnderpearlCooldown().setForceExpired(true);
					player.sendMessage(CC.translate("&dEnderpearl cooldown cleared!"));
				}
			}
			else if (args[1].equalsIgnoreCase("event")) {
				EventGame.setCooldown(new Cooldown(0));
				player.sendMessage(CC.translate("&dEvent cooldown cleared!"));
			}
		}
	}
}
