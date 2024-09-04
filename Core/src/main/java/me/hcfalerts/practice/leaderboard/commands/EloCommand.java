package me.hcfalerts.practice.leaderboard.commands;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.find.ProfileFinder;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EloCommand extends BaseCommand {

    @Command(name = "elo", permission = "practice.command.elo")
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
            for (String s : HCFPractice.get().getLangConfig().getStringList("ELO.VIEW_OTHER")) {
                if (s.contains("{format}")) {
                    for (Kit kit : Kit.getKits()) {
                        if (kit.isEnabled()) {
                            if (kit.getGameRules().isRanked()) {
                                player.sendMessage(CC.translate(HCFPractice.get().getLangConfig().getString("ELO.VIEW_FORMAT")
                                        .replace("{kit}", kit.getName())
                                        .replace("{elo}", String.valueOf(target.getKitData().get(kit).getElo()))));
                            }
                        }
                    }
                    continue;
                }
                player.sendMessage(CC.translate(s
                        .replace("{bars}", CC.CHAT_BAR)
                        .replace("{color}", target.getColor())
                        .replace("{player}", target.getName())));
            }
            return;
        }

        for (String s : HCFPractice.get().getLangConfig().getStringList("ELO.VIEW_YOUR")) {
            if (s.contains("{format}")) {
                for (Kit kit : Kit.getKits()) {
                    if (kit.isEnabled()) {
                        if (kit.getGameRules().isRanked()) {
                            player.sendMessage(CC.translate(HCFPractice.get().getLangConfig().getString("ELO.VIEW_FORMAT")
                                    .replace("{kit}", kit.getName())
                                    .replace("{elo}", String.valueOf(Profile.get(player.getUniqueId()).getKitData().get(kit).getElo()))));
                        }
                    }
                }
                continue;
            }
            player.sendMessage(CC.translate(s.replace("{bars}", CC.CHAT_BAR)));
        }
    }
}
