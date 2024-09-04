package me.hcfalerts.practice.event.command;

import me.hcfalerts.practice.event.Event;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.chat.ChatComponentBuilder;
import me.hcfalerts.practice.utilities.chat.ChatHelper;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class EventsCommand extends BaseCommand {

	@Command(name = "events", permission = "practice.event.host")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.sendMessage(CC.PINK + CC.BOLD + "Events" + CC.GRAY + ":");
		for (Event events : Event.events) {
			ChatComponentBuilder builder = new ChatComponentBuilder("")
					.parse("&7- " + "&a" + events.getName());

			ChatComponentBuilder status = new ChatComponentBuilder("").parse("&7[&fSTATUS&7]");
			status.attachToEachPart(ChatHelper.hover("&fClick to view this event's status."));
			status.attachToEachPart(ChatHelper.click("/event info"));

			builder.append(" ");

			for (BaseComponent component : status.create()) {
				builder.append((TextComponent) component);
			}

			player.spigot().sendMessage(builder.create());
		}
	}
}
