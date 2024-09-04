package me.hcfalerts.practice.leaderboard.commands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.leaderboard.menu.StatisticsMenu;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.find.ProfileFinder;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StatsCommand extends BaseCommand {

    @Command(name = "stats")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length > 0) {
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
            new StatisticsMenu(target).openMenu(player);
            return;
        }

        new StatisticsMenu(Profile.get(player.getUniqueId())).openMenu(player);
    }
}