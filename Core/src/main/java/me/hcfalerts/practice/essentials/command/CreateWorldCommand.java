package me.hcfalerts.practice.essentials.command;

import com.google.common.collect.Lists;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.util.List;
import java.util.Random;

public class CreateWorldCommand extends BaseCommand {

	public static List<String> VOID_WORLDS = Lists.newArrayList();

	@Command(name = "createvoidworld", permission = "practice.command.createvoidworld")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please insert a world name.");
			return;
		}
		else if (args.length == 1) {
			player.sendMessage(CC.RED + "Please insert a type <normal|nether|the_end>");
			return;
		}

		String nameWorld = args[0];
		String type = args[1];
		WorldCreator worldCreator = new WorldCreator(nameWorld)
				.generator(new ChunkGenerator() {
					@Override
					public byte[] generate(World world, Random random, int x, int z) {
						return new byte[32768];
					}
				});
		World world = null;
		if (type.equalsIgnoreCase("normal")) {
			worldCreator.environment(World.Environment.NORMAL);
			Bukkit.getServer().createWorld(worldCreator);
			VOID_WORLDS.add(nameWorld);
		}
		else if (type.equalsIgnoreCase("nether")) {
			worldCreator.environment(World.Environment.NETHER);
			Bukkit.getServer().createWorld(worldCreator);
			VOID_WORLDS.add(nameWorld);
		}
		else if (type.equalsIgnoreCase("the_end")) {
			worldCreator.environment(World.Environment.THE_END);
			Bukkit.getServer().createWorld(worldCreator);
			VOID_WORLDS.add(nameWorld);
		}
		else {
			player.sendMessage(CC.RED + "Please insert a valid type <normal|nether|the_end>");
			return;
		}
		player.sendMessage(CC.GREEN + "Creating World...");
	}
}
