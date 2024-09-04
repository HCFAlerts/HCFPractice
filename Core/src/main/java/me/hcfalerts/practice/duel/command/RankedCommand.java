package me.hcfalerts.practice.duel.command;

import me.hcfalerts.practice.queue.menu.QueueSelectKitMenu;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class RankedCommand extends BaseCommand {

    @Command(name = "ranked")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        new QueueSelectKitMenu(true).openMenu(player);
    }
}
