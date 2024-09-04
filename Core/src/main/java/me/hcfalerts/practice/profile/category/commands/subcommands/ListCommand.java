package me.hcfalerts.practice.profile.category.commands.subcommands;

import me.hcfalerts.practice.profile.category.Category;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ListCommand extends BaseCommand {

    @Command(name = "category.list", permission = "practice.command.category")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        player.sendMessage(CC.translate(CC.CHAT_BAR));
        player.sendMessage(CC.translate("&d&lCategory List"));
        player.sendMessage("");
        for (Category category : Category.getCategories()) {
            player.sendMessage(CC.translate("&7- " + category.getDisplayName() + " &7(" + category.getName() + ")" + " &7(ELO: &f" + category.getElo() + ")"));
        }
        player.sendMessage(CC.translate(CC.CHAT_BAR));
    }
}
