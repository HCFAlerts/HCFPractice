package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ListWorldsCommand extends BaseCommand {

    @Command(name = "listworlds", aliases = {"listworld"}, permission = "practice.command.listworlds")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        player.sendMessage(CC.DARK_GREEN + "List Worlds");
        for (World world : Bukkit.getWorlds()) {
            player.sendMessage(CC.GRAY + "- " + CC.GREEN + world.getName() + CC.GRAY + " (" + CC.GREEN + world.getEnvironment().name() + CC.GRAY + ")");
        }
    }
}
