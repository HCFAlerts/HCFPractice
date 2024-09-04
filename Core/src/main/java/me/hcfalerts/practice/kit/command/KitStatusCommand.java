package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class KitStatusCommand extends BaseCommand {

	@Command(name = "kit.status", permission = "practice.kit.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please usage: /kit status <kit>");
			return;
		}

		Kit kit = Kit.getByName(args[0]);
		if (kit == null) {
			player.sendMessage(CC.RED + "A kit with that name does not exist.");
			return;
		}
		player.sendMessage(CC.CHAT_BAR);
		player.sendMessage(CC.translate("&d&lKits Status &7(" + (kit.isEnabled() ? "&a" : "&c") + kit.getName() + "&7)"));
		player.sendMessage("");
		player.sendMessage(CC.translate("&fRanked &7» " + (kit.getGameRules().isRanked() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fBuild &7» " + (kit.getGameRules().isBuild() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fSpleef &7» " + (kit.getGameRules().isSpleef() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fSumo &7» " + (kit.getGameRules().isSumo() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fParkour &7» " + (kit.getGameRules().isParkour() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fHCF &7» " + (kit.getGameRules().isHcf() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fBridge &7» " + (kit.getGameRules().isBridge() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fBoxing &7» " + (kit.getGameRules().isBoxing() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fSkyWars &7» " + (kit.getGameRules().isSkywars() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fHCFTrap &7» " + (kit.getGameRules().isHcfTrap() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fNo Fall Damage &7» " + (kit.getGameRules().isNoFallDamage() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fNo Food &7» " + (kit.getGameRules().isNoFood() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fSoup &7» " + (kit.getGameRules().isSoup() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fHealth Regeneration &7» " + (kit.getGameRules().isHealthRegeneration() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fShow Health &7» " + (kit.getGameRules().isShowHealth() ? "&a\u2713" : "&c\u2717")));
		player.sendMessage(CC.translate("&fHit Delay &7» " + kit.getGameRules().getHitDelay()));
		player.sendMessage(CC.translate("&fKb Profile &7» " + kit.getGameRules().getKbProfile()));
		player.sendMessage(CC.translate("&fEffects&7: "));
		for (PotionEffect effect : kit.getGameRules().getEffects()) {
			player.sendMessage(CC.translate(" &7» &f" + effect.getType().getName() + " &7| " + effect.getDuration() + "s"));
		}
		player.sendMessage(CC.CHAT_BAR);
	}
}
