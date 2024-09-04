package me.hcfalerts.practice.profile.meta.option.command;

import me.hcfalerts.practice.profile.meta.option.menu.ProfileOptionsMenu;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class OptionsCommand extends BaseCommand {

	@Command(name = "options", aliases = {"settings"})
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		new ProfileOptionsMenu().openMenu(player);
	}
}
