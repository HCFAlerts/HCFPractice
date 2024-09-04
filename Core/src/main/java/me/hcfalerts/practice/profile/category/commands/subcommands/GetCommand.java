package me.hcfalerts.practice.profile.category.commands.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.find.ProfileFinder;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class GetCommand extends BaseCommand {

    @Command(name = "category.get", permission = "practice.command.category")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.translate("&cUse /category get <player>"));
            return;
        }

        String playerName = args[0];

        if (ProfileFinder.findProfileByName(playerName) == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND
                        .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }

        Profile profile = ProfileFinder.findProfileByName(playerName);

        profile.updateCategory();

        player.sendMessage(CC.translate("&7" + profile.getName() + " has &r" + profile.getCategory().getDisplayName() + " &7category."));
    }
}
