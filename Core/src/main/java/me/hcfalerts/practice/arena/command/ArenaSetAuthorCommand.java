package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ArenaSetAuthorCommand extends BaseCommand {

    @Command(name = "arena.setauthor", permission = "practice.arena.admin")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length < 2) {
            player.sendMessage(CC.translate("&cUsage: /arena setauthor <arena> <author>"));
            return;
        }

        Arena arena = null;
        for (Arena val : Arena.getArenas()) {
            if (val.getName().equalsIgnoreCase(args[0])) {
                arena = val;
                break;
            }
        }
        String author = args[1];

        if (arena == null) {
            player.sendMessage(CC.translate("&cPlease usage a valid arena name"));
            return;
        }

        arena.setAuthor(author);
        player.sendMessage(CC.translate("&dAuthor of &f" + arena.getName() + "&d has seen set to &f" + author + "&d."));
        arena.save();
    }
}