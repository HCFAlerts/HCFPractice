package me.hcfalerts.practice.party.command.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyUnbanCommand extends BaseCommand {

    @Command(name = "party.unban", aliases = {"p.unban"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "Please usage: /party unban (leader)");
            return;
        }

        Profile profile = Profile.get(player.getUniqueId());
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        if (profile.getParty() == null) {
            player.sendMessage(CC.RED + "You do not have a party.");
            return;
        }

        if (!profile.getParty().getLeader().equals(player)) {
            player.sendMessage(CC.RED + "You are not the leader of your party.");
            return;
        }

        Party party = profile.getParty();
        if (party.getBannedPlayers().remove(target.getUniqueId())) {
            for (Player listOfPlayer : party.getListOfPlayers()) {
                listOfPlayer.sendMessage(ChatColor.RED + target.getName() + " has been unbanned from your party.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "This player is not banned from your party.");
        }
    }
}