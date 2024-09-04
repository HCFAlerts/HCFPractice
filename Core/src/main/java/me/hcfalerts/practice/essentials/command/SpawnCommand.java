package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.PlayerUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class SpawnCommand extends BaseCommand {

	@Command(name = "spawn", permission = "practice.command.spawn")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		Profile profile = Profile.get(player.getUniqueId());

		PlayerUtil.reset(player);
		profile.setState(ProfileState.LOBBY);
		HCFPractice.get().getEssentials().teleportToSpawn(player);
		VisibilityLogic.handle(player);
		Hotbar.giveHotbarItems(player);

		player.sendMessage(CC.GREEN + "You teleported to the spawn.");
	}
}
