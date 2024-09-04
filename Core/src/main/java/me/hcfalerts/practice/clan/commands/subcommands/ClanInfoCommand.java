package me.hcfalerts.practice.clan.commands.subcommands;

import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClanInfoCommand extends BaseCommand {

    @Command(name = "clan.info", aliases = {"clan.show", "clan.i"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            Profile profile = Profile.get(player.getUniqueId());

            if (profile.getClan() != null) profile.getClan().show(player);
            else player.sendMessage(ChatColor.GRAY + "/clan info (name)");
            return;
        }

        Clan clan = Clan.getByName(args[0]);
        clan.show(player);
    }
}
