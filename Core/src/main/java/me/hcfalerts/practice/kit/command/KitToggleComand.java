package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitToggleComand extends BaseCommand {

    @Command(name = "kit.toggle", permission = "practice.kit.admin")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "Please usage: /kit toggle <kit>");
            return;
        }

        Kit kit = Kit.getByName(args[0]);
        if (kit == null) {
            player.sendMessage(CC.RED + "This kit doesn't exist.");
            return;
        }

        kit.setEnabled(!kit.isEnabled());
        kit.save();
        player.sendMessage(ChatColor.GREEN + "Kit is now " + (kit.isEnabled() ? "enabled" : "disabled"));
    }
}