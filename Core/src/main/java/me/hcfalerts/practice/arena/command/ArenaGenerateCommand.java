package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.arena.ArenaType;
import me.hcfalerts.practice.arena.generator.ArenaGenerator;
import me.hcfalerts.practice.arena.generator.Schematic;
import me.hcfalerts.practice.arena.impl.StandaloneArena;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Objects;

public class ArenaGenerateCommand extends BaseCommand {

	@Command(name = "arena.generate", permission = "practice.arena.admin")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();

		File schematicsFolder = new File(HCFPractice.get().getDataFolder().getPath() + File.separator + "schematics");

		if (!schematicsFolder.exists()) {
			player.sendMessage(CC.RED + "The \"schematics\" folder doesn't exist.");
			return;
		}

		for (File file : Objects.requireNonNull(schematicsFolder.listFiles())) {
			if (!file.isDirectory() && file.getName().contains(".schematic")) {
				boolean duplicate = file.getName().endsWith("_duplicate.schematic");

				String name = file.getName()
						.replace(".schematic", Strings.EMPTY)
						.replace("_duplicate", Strings.EMPTY);

				Arena parent = Arena.getByName(name);

				if (parent != null) {
					if (!(parent instanceof StandaloneArena)) {
						System.out.println("Skipping " + name + " because it's not duplicate and an arena with that name already exists.");
						continue;
					}
				}

				TaskUtil.run(() -> {
					try {
						new ArenaGenerator(name, new Schematic(file), duplicate ?
								(parent != null ? ArenaType.DUPLICATE : ArenaType.STANDALONE) : ArenaType.SHARED)
								.generate(file, (StandaloneArena) parent);
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		}

		player.sendMessage(CC.GREEN + "Generating arenas... See console for details.");
	}
}
