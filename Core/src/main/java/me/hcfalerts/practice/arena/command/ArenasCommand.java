package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.arena.ArenaType;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.chat.ChatComponentBuilder;
import me.hcfalerts.practice.utilities.chat.ChatHelper;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class ArenasCommand extends BaseCommand {

	@Command(name = "arenas", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.sendMessage(CC.translate("&d&lArenas&7:"));

		if (Arena.getArenas().isEmpty()) {
			player.sendMessage(CC.RED + "There are no arenas.");
			return;
		}

		for (Arena arena : Arena.getArenas()) {
			if (arena.getType() != ArenaType.DUPLICATE) {
				ChatComponentBuilder builder = new ChatComponentBuilder("")
						.parse("&7- " + (arena.isSetup() ? "&a" : "&c") + arena.getName() +
								" &7(" + arena.getType().name() + ")");

				ChatComponentBuilder status = new ChatComponentBuilder("").parse("&7[&fSTATUS&7]");
				status.attachToEachPart(ChatHelper.hover("&fClick to view this arena's status."));
				status.attachToEachPart(ChatHelper.click("/arena status " + arena.getName()));

				builder.append(" ");

				for (BaseComponent component : status.create()) {
					builder.append((TextComponent) component);
				}

				player.spigot().sendMessage(builder.create());
			}
		}
	}

}
