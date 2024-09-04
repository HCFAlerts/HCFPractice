package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.profile.menu.LangMenu;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class LangCommand extends BaseCommand {

    @Command(name = "lang")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        new LangMenu().openMenu(player);
    }
}