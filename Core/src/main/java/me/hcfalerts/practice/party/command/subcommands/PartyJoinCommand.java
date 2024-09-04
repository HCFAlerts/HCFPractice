package me.hcfalerts.practice.party.command.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.party.PartyPrivacy;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyJoinCommand extends BaseCommand {

	@Command(name = "party.join", aliases = {"p.join"})
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /party join (leader)");
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

		if (profile.getParty() != null) {
			player.sendMessage(CC.RED + "You already have a party.");
			return;
		}

		Profile targetProfile = Profile.get(target.getUniqueId());
		Party party = targetProfile.getParty();

		if (party == null) {
			player.sendMessage(CC.RED + "A party with that name could not be found.");
			return;
		}

		if (party.getPrivacy() == PartyPrivacy.CLOSED) {
			if (party.getInvite(player.getUniqueId()) == null) {
				player.sendMessage(CC.RED + "You have not been invited to that party.");
				return;
			}
		}

		if (party.getPlayers().size() >= 32) {
			player.sendMessage(CC.RED + "That party is full and cannot hold anymore players.");
			return;
		}

		if(party.getBannedPlayers().contains(player.getUniqueId())){
			player.sendMessage(ChatColor.RED + "You are banned from this party, you cannot enter.");
			return;
		}

		party.join(player);
	}
}
