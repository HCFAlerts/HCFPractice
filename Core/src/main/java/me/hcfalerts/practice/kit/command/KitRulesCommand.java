package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitRulesCommand extends BaseCommand {

	@Command(name = "kit.rules", permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		player.sendMessage(CC.CHAT_BAR);
		player.sendMessage(CC.translate("&d&lKit Rules &7(1/1)"));
		player.sendMessage(CC.translate(""));
		player.sendMessage(CC.translate("&a● &7Recommended to Gamemodes Kit's"));
		player.sendMessage(CC.translate("&6● &7Recommended to Specific Use"));
		player.sendMessage(CC.translate("&c● &7Not Recommended to Use [Beta Feature]"));
		player.sendMessage(CC.translate(""));
		player.sendMessage(CC.translate(" &a● &fbuild <true|false>"));
		player.sendMessage(CC.translate(" &a● &fspleef <true|false>"));
		player.sendMessage(CC.translate(" &a● &fsumo <true|false>"));
		player.sendMessage(CC.translate(" &a● &fbridge <true|false>"));
		player.sendMessage(CC.translate(" &a● &fboxing <true|false>"));
		player.sendMessage(CC.translate(" &a● &fskywars <true|false>"));
		player.sendMessage(CC.translate(" &a● &fsoup <true|false>"));
		player.sendMessage(CC.translate(" &6● &franked <true|false>"));
		player.sendMessage(CC.translate(" &6● &fhcf <true|false> &7[Rally Command]"));
		player.sendMessage(CC.translate(" &6● &fnofall <true|false>"));
		player.sendMessage(CC.translate(" &6● &fnofood <true|false>)"));
		player.sendMessage(CC.translate(" &6● &fhitdelay <20,19,etc>"));
		player.sendMessage(CC.translate(" &6● &fkbprofile <kb_name> &7[FoxSpigot Only]"));
		player.sendMessage(CC.translate(" &6● &fhcftrap <true|false> &7[BaseRaider, BaseTrapper]"));
		player.sendMessage(CC.translate(" &6● &fhealthregeneration <true|false>"));
		player.sendMessage(CC.translate(" &6● &fshowhealth <true|false>"));
		player.sendMessage(CC.translate(" &6● &feditoritems"));
		player.sendMessage(CC.translate(" &6● &feffects <effect|help>"));
		player.sendMessage(CC.translate(" &c● &fparkour <true|false>"));
		player.sendMessage(CC.CHAT_BAR);
	}
}
