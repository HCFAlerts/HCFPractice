package me.hcfalerts.practice.profile.category.commands;

import me.hcfalerts.practice.profile.category.commands.subcommands.*;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class CategoryCommand extends BaseCommand {

    public CategoryCommand() {
        super();
        new ListCommand();
        new CreateCommand();
        new RemoveCommand();
        new SetCommand();
        new GetCommand();
    }

    @Command(name = "category", permission = "practice.command.category")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        for (String s : Arrays.asList(
                CC.CHAT_BAR,
                "&d&lCategory Help &7(1/1)",
                "",
                "&f/category create <name> &7- &aAdd a new category",
                "&f/category remove <name> &7- &aRemove a category",
                "&f/category list &7- &aList all categories",
                "&f/category set <name> <type> <value> &7- &aSet a category value",
                "&f/category get <player> &7- &aGet a category of player",
                CC.CHAT_BAR
        )) {
            player.sendMessage(CC.translate(s));
        }
    }
}
