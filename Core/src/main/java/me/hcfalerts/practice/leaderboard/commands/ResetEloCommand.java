package me.hcfalerts.practice.leaderboard.commands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.find.ProfileFinder;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResetEloCommand extends BaseCommand {

    @Command(name = "resetelo", permission = "practice.command.resetelo")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "Please usage: /resetelo (player)");
            return;
        }

        Profile target;
        if (Bukkit.getPlayer(args[0]) != null) {
            target = Profile.get(Bukkit.getPlayer(args[0]).getUniqueId());
        } else {
            target = ProfileFinder.findProfileByName(args[0]);
        }
        if (target == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }

        target.getKitData().forEach((kit, profileKitData) -> {
            profileKitData.setElo(1000);
        });
        target.updateCategory();
        if (!target.isOnline()) TaskUtil.runAsync(target::save);
        player.sendMessage(CC.translate("&a" + target.getName() + " reset elo successfully"));
    }
}
