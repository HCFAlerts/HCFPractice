package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.BukkitReflection;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.chat.StyleUtil;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand extends BaseCommand {

    @Command(name = "ping")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.GOLD + "Your Ping: " + StyleUtil.colorPing(BukkitReflection.getPing(player)));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }
        player.sendMessage(CC.translate(Profile.get(target.getUniqueId()).getColor() + target.getName() + CC.GOLD + "'s Ping: " +
                StyleUtil.colorPing(BukkitReflection.getPing(target))));
    }
}
