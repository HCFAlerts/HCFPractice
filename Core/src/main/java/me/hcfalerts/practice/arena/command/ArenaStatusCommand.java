package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;

public class ArenaStatusCommand extends BaseCommand {

	@Command(name = "arena.status", permission = "practice.arena.admin", inGameOnly = false)
	@Override
	public void onCommand(CommandArgs commandArgs) {
		CommandSender sender = commandArgs.getSender();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			sender.sendMessage(CC.RED + "Usage: /arena status <arena>");
			return;
		}

		Arena arena = Arena.getByName(args[0]);
		if (arena != null) {
			sender.sendMessage(CC.PINK + CC.BOLD + "Arena Status " + CC.GRAY + "(" +
					(arena.isSetup() ? CC.GREEN : CC.RED) + arena.getName() + CC.GRAY + ")");

			sender.sendMessage(CC.PINK + "Cuboid Lower Location: " + CC.WHITE +
					(arena.getLowerCorner() == null ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));

			sender.sendMessage(CC.PINK + "Cuboid Upper Location: " + CC.WHITE +
					(arena.getUpperCorner() == null ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));

			sender.sendMessage(CC.PINK + "Spawn A Location: " + CC.WHITE +
					(arena.getSpawnA() == null ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));

			sender.sendMessage(CC.PINK + "Spawn B Location: " + CC.WHITE +
					(arena.getSpawnB() == null ?
							StringEscapeUtils.unescapeJava("\u2717") :
							StringEscapeUtils.unescapeJava("\u2713")));

			sender.sendMessage(CC.PINK + "Kits: " + CC.WHITE + StringUtils.join(arena.getKits(), ", "));
		} else {
			sender.sendMessage(CC.RED + "An arena with that name does not exist.");
		}
	}
}
