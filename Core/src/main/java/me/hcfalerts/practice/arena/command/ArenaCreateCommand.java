package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.arena.ArenaType;
import me.hcfalerts.practice.arena.impl.SharedArena;
import me.hcfalerts.practice.arena.impl.StandaloneArena;
import me.hcfalerts.practice.arena.selection.Selection;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ArenaCreateCommand extends BaseCommand {

	@Command(name = "arena.create", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length < 2) {
			player.sendMessage(CC.translate("&cPlease usage: /arena create (name) (type)"));
			return;
		}

		String arenaName = args[0];
		ArenaType arenaType = null;
		for (ArenaType val : ArenaType.values()) {
			if (val.name().equalsIgnoreCase(args[1])) {
				arenaType = val;
				break;
			}
		}

		if (arenaType == null) {
			player.sendMessage(CC.translate("&cPlease usage a valid ArenaType: SHARED, STANDALONE"));
			return;
		}

		if (Arena.getByName(arenaName) == null) {
			Selection selection = Selection.createOrGetSelection(player);

			if (selection.isFullObject()) {
				if (arenaType == ArenaType.SHARED) {
					Arena arena = new SharedArena(arenaName, selection.getPoint1(), selection.getPoint2());
					Arena.getArenas().add(arena);

					player.sendMessage(CC.GREEN + "Created new arena \"" + arenaName + "\"");
				} else if (arenaType == ArenaType.STANDALONE) {
					Arena arena = new StandaloneArena(arenaName, selection.getPoint1(), selection.getPoint2());
					Arena.getArenas().add(arena);

					player.sendMessage(CC.GREEN + "Created new arena \"" + arenaName + "\"");
				}
			} else {
				player.sendMessage(CC.RED + "Your selection is incomplete.");
			}
		} else {
			player.sendMessage(CC.RED + "An arena with that name already exists.");
		}
	}

}
