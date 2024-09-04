package me.hcfalerts.practice.arena;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.arena.cuboid.Cuboid;
import me.hcfalerts.practice.arena.impl.SharedArena;
import me.hcfalerts.practice.arena.impl.StandaloneArena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.utilities.LocationUtil;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Arena extends Cuboid {

	@Getter private final static List<Arena> arenas = new ArrayList<>();

	@Getter protected String name;
	@Setter protected Location spawnA, spawnB;
	@Getter protected boolean busy;
	@Getter @Setter private List<String> kits = new ArrayList<>();
	@Getter @Setter private String author = "Unknown";
	@Getter private final List<Block> blocks = new ArrayList<>();

	public Arena(String name, Location location1, Location location2) {
		super(location1, location2);
		this.name = name;
	}

	public ArenaType getType() {
		return ArenaType.DUPLICATE;
	}

	public boolean isSetup() {
		return getLowerCorner() != null && getUpperCorner() != null && spawnA != null && spawnB != null;
	}

	public int getMaxBuildHeight() {
		int highest = (int) (Math.max(spawnA.getY(), spawnB.getY()));
		return highest + 5;
	}

	public Location getSpawnA() {
		if (spawnA == null) return null;

		return spawnA.clone();
	}

	public Location getSpawnB() {
		if (spawnB == null) return null;

		return spawnB.clone();
	}

	public void setBusy(boolean busy) {
		if (getType() != ArenaType.SHARED) this.busy = busy;
	}

	public void save() {

	}

	public void delete() {
		arenas.remove(this);
	}

	public static void init() {
		FileConfiguration configuration = HCFPractice.get().getArenasConfig().getConfiguration();

		if (configuration.contains("arenas")) {
			for (String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {
				String path = "arenas." + arenaName;

				ArenaType arenaType = ArenaType.valueOf(configuration.getString(path + ".type"));
				Location location1;
				Location location2;

				try {
					location1 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location1"));
					location2 = LocationUtil.deserialize(configuration.getString(path + ".cuboid.location2"));
				} catch (Exception e) {
					System.out.println("Location error, please check if the world is loading correctly");
					return;
				}

				Arena arena;

				if (arenaType == ArenaType.STANDALONE) arena = new StandaloneArena(arenaName, location1, location2);
				else if (arenaType == ArenaType.SHARED) arena = new SharedArena(arenaName, location1, location2);
				else continue;

				if (configuration.contains(path + ".spawnA"))
					arena.setSpawnA(LocationUtil.deserialize(configuration.getString(path + ".spawnA")));

				if (configuration.contains(path + ".spawnB"))
					arena.setSpawnB(LocationUtil.deserialize(configuration.getString(path + ".spawnB")));

				if (configuration.contains(path + ".author")) {
					String author = configuration.getString(path + ".author");
					arena.setAuthor(author);
				}

				if (configuration.contains(path + ".kits")) {
					for (String kitName : configuration.getStringList(path + ".kits")) {
						arena.getKits().add(kitName);
					}
				}

				if (arena instanceof StandaloneArena && configuration.contains(path + ".spawnred") &&
						configuration.contains(path + ".spawnblue")) {
					StandaloneArena standaloneArena = (StandaloneArena) arena;
					location1 = LocationUtil.deserialize(configuration.getString(path + ".spawnred.location1"));
					location2 = LocationUtil.deserialize(configuration.getString(path + ".spawnred.location2"));
					standaloneArena.setSpawnRed(new Cuboid(location1, location2));
					location1 = LocationUtil.deserialize(configuration.getString(path + ".spawnblue.location1"));
					location2 = LocationUtil.deserialize(configuration.getString(path + ".spawnblue.location2"));
					standaloneArena.setSpawnBlue(new Cuboid(location1, location2));
				}

				if (arena instanceof StandaloneArena && configuration.contains(path + ".duplicates")) {
					for (String duplicateId : configuration.getConfigurationSection(path + ".duplicates").getKeys(false)) {
						location1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".cuboid.location1"));
						location2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".cuboid.location2"));
						Location spawn1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawnA"));
						Location spawn2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawnB"));

						Arena duplicate = new Arena(arenaName, location1, location2);

						duplicate.setSpawnA(spawn1);
						duplicate.setSpawnB(spawn2);
						duplicate.setKits(arena.getKits());

						((StandaloneArena) arena).getDuplicates().add(duplicate);

						arenas.add(duplicate);
					}
				}

				arenas.add(arena);
			}
		}

		for (Arena arena : arenas) {
			for (Chunk chunk : arena.getChunks()) {
				chunk.load();
			}
		}
	}

	public static Arena getByName(String name) {
		for (Arena arena : arenas) {
			if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null &&
			    arena.getName().equalsIgnoreCase(name)) {
				return arena;
			}
		}

		return null;
	}

	public static Arena getRandomArena(Kit kit) {
		List<Arena> selections = new ArrayList<>();

		for (Arena arena : arenas) {
			if (!arena.isSetup() || !arena.getKits().contains(kit.getName()) || arena.isBusy()) continue;

			if (kit.isStandaloneType() && (arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE)) {
				selections.add(arena);
			}
			else if (!kit.isStandaloneType() && arena.getType() == ArenaType.SHARED) {
				selections.add(arena);
			}
		}

		if (selections.isEmpty()) return null;

		return selections.get(ThreadLocalRandom.current().nextInt(selections.size()));
	}
}
