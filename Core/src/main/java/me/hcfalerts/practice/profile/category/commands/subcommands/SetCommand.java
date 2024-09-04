package me.hcfalerts.practice.profile.category.commands.subcommands;

import me.hcfalerts.practice.profile.category.Category;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class SetCommand extends BaseCommand {

    @Command(name = "category.set", permission = "practice.command.category")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length < 3) {
            player.sendMessage(CC.translate("&cUse /category set <category> <type> <value>"));
            return;
        }

        String categoryArg = args[0];
        String type = args[1];
        if (Category.getByName(categoryArg) == null) {
            player.sendMessage(CC.translate("&cCategory &f" + categoryArg + " &cnot found."));
            return;
        }
        Category category = Category.getByName(categoryArg);

        if (!type.equalsIgnoreCase("displayname") && !type.equalsIgnoreCase("elo")) {
            player.sendMessage(CC.translate("&cType &f" + type + " &cnot found (You only can set \"displayname\" or \"elo\"."));
        } else {
            if (type.equalsIgnoreCase("displayname")) {
                String displayname = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                category.setDisplayName(displayname);
                player.sendMessage(CC.translate("&aCategory &f" + categoryArg + " &adisplayname setted to &r" + displayname));
            }
            else if (type.equalsIgnoreCase("elo")) {
                String arg = args[2];
                if (!StringUtils.isNumeric(arg)) {
                    player.sendMessage(CC.translate("&cElo must be a number."));
                    return;
                }
                int elo = Integer.parseInt(arg);
                if (elo < 0) {
                    player.sendMessage(CC.translate("&cElo must be greater than 0."));
                    return;
                }
                category.setElo(elo);
                player.sendMessage(CC.translate("&aCategory &f" + categoryArg + " &aelo setted to &r" + elo));
            }
        }
    }
}
