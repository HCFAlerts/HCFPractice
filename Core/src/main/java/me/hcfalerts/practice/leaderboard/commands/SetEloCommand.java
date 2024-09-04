package me.hcfalerts.practice.leaderboard.commands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.kit.Kit;
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

import java.util.Arrays;
import java.util.regex.Pattern;

public class SetEloCommand extends BaseCommand {

    private final Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    @Command(name = "setelo", permission = "practice.command.setelo")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (Arrays.asList(0,1,2).contains(args.length)) {
            player.sendMessage(CC.RED + "Please usage: /setelo (kit) (player) (integer)");
            return;
        }

        Kit kit = Kit.getByName(args[0]);
        if (kit == null) {
            player.sendMessage(CC.RED + "Please insert a valid Kit.");
            return;
        }

        Profile target;

        if (Bukkit.getPlayer(args[1]) != null) {
            target = Profile.get(Bukkit.getPlayer(args[1]).getUniqueId());
        } else {
            target = ProfileFinder.findProfileByName(args[1]);
        }

        if (target == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }

        if (!pattern.matcher(args[2]).matches()) {
            player.sendMessage(CC.RED + "Please insert a valid Integer.");
            return;
        }
        int integer = Integer.parseInt(args[2]);

        target.getKitData().get(kit).setElo(integer);
        target.updateCategory();
        if (!target.isOnline()) TaskUtil.runAsync(target::save);
        player.sendMessage(CC.translate("&cPlayer " + target.getName() + " new elo from " + kit.getName() + " is " + integer));
    }
}
