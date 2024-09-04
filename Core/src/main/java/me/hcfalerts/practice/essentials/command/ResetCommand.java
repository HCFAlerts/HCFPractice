package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.PlayerUtil;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ResetCommand extends BaseCommand {

    @Command(name = "reset", permission = "practice.command.reset")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            PlayerUtil.reset(player);
            Profile.get(player.getUniqueId()).setState(ProfileState.LOBBY);
            HCFPractice.get().getEssentials().teleportToSpawn(player);
            VisibilityLogic.handle(player);
            Hotbar.giveHotbarItems(player);
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }

        PlayerUtil.reset(target);
        Profile.get(target.getUniqueId()).setState(ProfileState.LOBBY);
        HCFPractice.get().getEssentials().teleportToSpawn(target);
        VisibilityLogic.handle(target);
        Hotbar.giveHotbarItems(target);
    }
}
