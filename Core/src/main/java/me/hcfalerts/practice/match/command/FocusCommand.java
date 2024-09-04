package me.hcfalerts.practice.match.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.lunar.BukkitAPI;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FocusCommand extends BaseCommand {

    @Command(name = "focus", aliases = {"f.focus"})
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        String[] args = command.getArgs();
        Profile profile = Profile.get(player.getUniqueId());

        if (args.length == 0) {
            player.sendMessage(CC.RED + "Usage: /focus <player>");
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND.format(profile.getLocale())).send(player);
            return;
        }

        if (!BukkitAPI.isRunning(player)) {
            new MessageFormat(Locale.LUNAR_CLIENT_NOT_RUNNING.format(profile.getLocale())).send(player);
            return;
        }

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = profile.getMatch();

            if (match.getKit().getGameRules().isHcf()) {
                profile.setFocused(target);
                BukkitAPI.sendTeammates(player, target);
                new MessageFormat(Locale.DUEL_FOCUSED_MESSAGE.format(profile.getLocale())).send(player);
            } else {
                new MessageFormat(Locale.DUEL_NOT_HCF_KIT.format(profile.getLocale())).send(player);
            }
        } else {
            new MessageFormat(Locale.DUEL_NOT_IN_MATCH.format(profile.getLocale())).send(player);
        }
    }
}
