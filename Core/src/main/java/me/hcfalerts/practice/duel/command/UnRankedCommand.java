package me.hcfalerts.practice.duel.command;

import me.hcfalerts.practice.queue.menu.QueueSelectKitMenu;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class UnRankedCommand extends BaseCommand {

    @Command(name = "unranked")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        new QueueSelectKitMenu(false).openMenu(player);
    }
}
