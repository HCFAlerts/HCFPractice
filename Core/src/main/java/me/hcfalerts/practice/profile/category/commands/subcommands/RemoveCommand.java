package me.hcfalerts.practice.profile.category.commands.subcommands;

import me.hcfalerts.practice.profile.category.Category;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class RemoveCommand extends BaseCommand {

    @Command(name = "category.remove", permission = "practice.command.category")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 1) {
            player.sendMessage(CC.translate("&cUsage: /category remove <category>"));
            return;
        }

        String category = args[0];
        if (Category.getByName(category) == null) {
            player.sendMessage(CC.translate("&cCategory &f" + category + " &cnot found."));
            return;
        }

        Category.getCategories().remove(Category.getByName(category));
        player.sendMessage(CC.translate("&aCategory &f" + category + " &aremoved."));
    }
}
