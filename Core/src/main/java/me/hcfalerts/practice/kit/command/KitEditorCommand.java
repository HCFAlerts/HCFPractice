package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.menu.KitMainEditorMenu;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitEditorCommand extends BaseCommand {

    @Command(name = "kit.editor", aliases = {"kit.editormenu"}, permission = "practice.command.kit.editor")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        new KitMainEditorMenu().openMenu(player);
    }
}
