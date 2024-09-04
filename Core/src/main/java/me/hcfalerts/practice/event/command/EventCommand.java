package me.hcfalerts.practice.event.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.event.game.command.*;
import me.hcfalerts.practice.event.game.map.command.EventMapCommand;
import me.hcfalerts.practice.event.game.map.command.EventMapsCommand;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class EventCommand extends BaseCommand {

	public EventCommand() {
		super();
		new EventAddMapCommand();
		new EventAdminCommand();
		new EventRemoveMapCommand();
		new EventSetLobbyCommand();
		new EventCancelCommand();
		new EventClearCooldownCommand();
		new EventForceStartCommand();
		new EventInfoCommand();
		new EventJoinCommand();
		new EventLeaveCommand();
		new EventMapCommand();
		new EventMapsCommand();
	}

	@Command(name = "event")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		new MessageFormat(Locale.EVENT_HELP.format(Profile.get(player.getUniqueId()).getLocale())).send(player);
	}
}
