package me.hcfalerts.practice.party.command.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class PartyCreateCommand extends BaseCommand {

	@Command(name = "party.create", aliases = {"p.create"})
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		Profile profile = Profile.get(player.getUniqueId());

		if (profile.getParty() != null) {
			player.sendMessage(CC.RED + "You already have a party.");
			return;
		}

		if (profile.getState() != ProfileState.LOBBY) {
			player.sendMessage(CC.RED + "You must be in the lobby to create a party.");
			return;
		}

		profile.setParty(new Party(player));

		Hotbar.giveHotbarItems(player);

		new MessageFormat(Locale.PARTY_CREATE
				.format(profile.getLocale()))
				.send(player);
	}
}
