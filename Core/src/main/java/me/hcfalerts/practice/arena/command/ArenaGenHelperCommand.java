package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class ArenaGenHelperCommand extends BaseCommand {

	@Command(name = "arena.genhelper", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		Block origin = player.getLocation().getBlock();
		Block up = origin.getRelative(BlockFace.UP);

		origin.setType(Material.SPONGE);
		up.setType(Material.SIGN_POST);

		if (up.getState() instanceof Sign) {
			Sign sign = (Sign) up.getState();
			sign.setLine(0, ((int) player.getLocation().getPitch()) + "");
			sign.setLine(1, ((int) player.getLocation().getYaw()) + "");
			sign.update();

			player.sendMessage(CC.GREEN + "Generator helper placed.");
		}
	}
}
