package me.hcfalerts.practice.profile.category.commands.subcommands;

import me.hcfalerts.practice.profile.category.Category;
import me.hcfalerts.practice.profile.category.data.CategoryEditorData;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class CreateCommand extends BaseCommand {

    @Command(name = "category.create", permission = "practice.command.category")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length != 1) {
            player.sendMessage(CC.translate("&cUsage: /category create <name>"));
            return;
        }

        String name = args[0];
        if (Category.getByName(name) != null) {
            player.sendMessage(CC.translate("&cCategory with name &f" + name + "&c already exists!"));
            return;
        }
        Category category = new Category(name);
        if (name.length() < 3) {
            player.sendMessage(CC.translate("&cName must be at least 3 characters long."));
        }
        else if (name.length() > 16) {
            player.sendMessage(CC.translate("&cName must be at most 16 characters long."));
        }
        else {
            Category.getCategoryEditor().add(new CategoryEditorData(player.getUniqueId(), category, "displayname"));
            player.sendMessage(CC.translate("&cNow, insert the DisplayName in the chat (Remember you can use the characters for colors)"));
        }
    }
}
