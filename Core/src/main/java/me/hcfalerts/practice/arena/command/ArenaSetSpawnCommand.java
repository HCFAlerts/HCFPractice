package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.arena.cuboid.Cuboid;
import me.hcfalerts.practice.arena.impl.StandaloneArena;
import me.hcfalerts.practice.arena.selection.Selection;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ArenaSetSpawnCommand extends BaseCommand {

	@Command(name = "arena.setspawn", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length < 2) {
			player.sendMessage(CC.translate("&cPlease usage: /arena setspawn <arena> <a|b|red|blue>"));
			return;
		}
		Arena arena = Arena.getByName(args[0]);
		String pos = args[1];

		if (arena != null) {
			if (pos.equalsIgnoreCase("a")) {
				arena.setSpawnA(player.getLocation());
			} else if (pos.equalsIgnoreCase("b")) {
				arena.setSpawnB(player.getLocation());
			}else if (pos.equalsIgnoreCase("red")) {
				if(!(arena instanceof StandaloneArena)){
					player.sendMessage("Only StandAloneArena allow this");
					return;
				}
				StandaloneArena standaloneArena = (StandaloneArena) arena;
				Selection selection = Selection.createOrGetSelection(player);
				if (!selection.isFullObject()) {
					player.sendMessage(CC.RED + "Your selection is incomplete.");
					return;
				}
				standaloneArena.setSpawnRed(new Cuboid(selection.getPoint1(), selection.getPoint2()));
			}else if (pos.equalsIgnoreCase("blue")) {
				if(!(arena instanceof StandaloneArena)){
					player.sendMessage("Only StandAloneArena allow this");
					return;
				}
				StandaloneArena standaloneArena = (StandaloneArena) arena;
				Selection selection = Selection.createOrGetSelection(player);
				if (!selection.isFullObject()){
					player.sendMessage(CC.RED + "Your selection is incomplete.");
					return;
				}
				standaloneArena.setSpawnBlue(new Cuboid(selection.getPoint1(), selection.getPoint2()));
			} else {
				player.sendMessage(CC.RED + "Invalid spawn point. Try \"a\" or \"b\".");
				return;
			}

			arena.save();

			player.sendMessage(CC.GREEN + "Updated spawn point \"" + pos + "\" for arena \"" + arena.getName() + "\"");
		} else {
			player.sendMessage(CC.RED + "An arena with that name doesn't exists.");
		}
	}
}
