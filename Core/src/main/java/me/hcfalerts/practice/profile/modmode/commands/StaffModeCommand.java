package me.hcfalerts.practice.profile.modmode.commands;

import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.modmode.ModMode;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class StaffModeCommand extends BaseCommand {

    @Command(name = "staffmode", aliases = {"staff", "mod", "h"}, permission = "practice.command.staffmode")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.isBusy() && !ModMode.getStaffmode().contains(player.getUniqueId())) {
            player.sendMessage(CC.RED + "You cannot use this command while busy!");
            return;
        }

        if (profile.getState() == ProfileState.STAFF_MODE) ModMode.remove(player);
        else ModMode.add(player);
    }
}
