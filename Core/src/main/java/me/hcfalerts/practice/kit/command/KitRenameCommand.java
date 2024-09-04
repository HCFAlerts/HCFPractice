package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitRenameCommand extends BaseCommand {

    @Command(name = "kit.rename", description = "Rename a kit", permission = "practice.kit.rename")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length < 2) {
            player.sendMessage(CC.RED + "Please usage: /kit rename <kit> <new_name>");
            return;
        }

        Kit kit = Kit.getByName(args[0]);
        if (kit == null) {
            player.sendMessage(CC.RED + "Kit not found!");
            return;
        }

        kit.rename(args[1]);
        player.sendMessage(CC.GREEN + "Kit " + args[0] + " renamed to " + args[1]);
    }
}
