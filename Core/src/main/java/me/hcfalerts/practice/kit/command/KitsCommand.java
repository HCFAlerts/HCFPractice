package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.chat.ChatComponentBuilder;
import me.hcfalerts.practice.utilities.chat.ChatHelper;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitsCommand extends BaseCommand {

	@Command(name = "kits", permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.sendMessage(CC.translate("&d&lKits &7(1/1)"));

		for (Kit kit : Kit.getKits()) {
			ChatComponentBuilder builder = new ChatComponentBuilder("")
					.parse("&7- " + (kit.isEnabled() ? "&a" : "&c") + kit.getName() +
							" &7(" + (kit.getGameRules().isRanked() ? "Ranked" : "Un-Ranked") + ")");

			ChatComponentBuilder status = new ChatComponentBuilder("").parse("&7[&fSTATUS&7]");
			status.attachToEachPart(ChatHelper.hover("&fClick to view this kit's status."));
			status.attachToEachPart(ChatHelper.click("/kit status " + kit.getName()));

			builder.append(" ");

			for (BaseComponent component : status.create()) {
				builder.append((TextComponent) component);
			}

			player.spigot().sendMessage(builder.create());
		}
	}
}
