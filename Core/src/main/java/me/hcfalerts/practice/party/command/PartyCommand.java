package me.hcfalerts.practice.party.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.party.PartyPrivacy;
import me.hcfalerts.practice.party.command.subcommands.*;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyCommand extends BaseCommand {

    public PartyCommand() {
        super();
        new PartyChatCommand();
        new PartyCloseCommand();
        new PartyCreateCommand();
        new PartyDisbandCommand();
        new PartyInfoCommand();
        new PartyInviteCommand();
        new PartyJoinCommand();
        new PartyKickCommand();
        new PartyLeaveCommand();
        new PartyOpenCommand();
        new PartyInviteClanCommand();
        new PartyBanCommand();
        new PartyUnbanCommand();
    }

    @Command(name = "party", aliases = {"p"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();
        Profile profile = Profile.get(player.getUniqueId());

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            new MessageFormat(Locale.PARTY_HELP
                    .format(profile.getLocale()))
                    .send(player);
        }
        else if (Bukkit.getPlayer(args[0]) != null) {
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

            if (profile.getParty().getInvite(target.getUniqueId()) != null) {
                player.sendMessage(CC.RED + "That player has already been invited to your party.");
                return;
            }

            if (profile.getParty().containsPlayer(target.getUniqueId())) {
                player.sendMessage(CC.RED + "That player is already in your party.");
                return;
            }

            if (profile.getParty().getPrivacy() == PartyPrivacy.OPEN) {
                player.sendMessage(CC.RED + "The party state is Open. You do not need to invite players.");
                return;
            }

            if(profile.getParty().getBannedPlayers().contains(player.getUniqueId())){
                player.sendMessage(ChatColor.RED + "You can't invite banned players from your party.");
                return;
            }

            Profile targetData = Profile.get(target.getUniqueId());

            if (targetData.isBusy()) {
                player.sendMessage(target.getDisplayName() + CC.RED + " is currently busy.");
                return;
            }

            profile.getParty().invite(target);
        }
    }
}
