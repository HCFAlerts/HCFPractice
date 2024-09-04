package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.selection.Selection;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ArenaSelectionCommand extends BaseCommand {

	@Command(name = "arena.wand", aliases = {"arena.selection"}, permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		if (player.getInventory().first(Selection.SELECTION_WAND) != -1) {
			player.getInventory().remove(Selection.SELECTION_WAND);
		} else {
			player.getInventory().addItem(Selection.SELECTION_WAND);
		}

		player.updateInventory();
	}
}
