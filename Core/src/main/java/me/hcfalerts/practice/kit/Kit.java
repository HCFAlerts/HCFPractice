package me.hcfalerts.practice.kit;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.kit.meta.KitEditRules;
import me.hcfalerts.practice.kit.meta.KitGameRules;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.utilities.InventoryUtil;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.PotionUtil;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Kit {

	@Getter private static final List<Kit> kits = new ArrayList<>();

	private String name;
	@Setter private boolean enabled;
	@Setter private ItemStack displayIcon;
	@Setter private int rankedSlot, unrankedSlot;
	private final KitLoadout kitLoadout = new KitLoadout();
	private final KitEditRules editRules = new KitEditRules();
	private final KitGameRules gameRules = new KitGameRules();

	public Kit(String name) {
		this.name = name;
		this.displayIcon = new ItemStack(Material.DIAMOND_SWORD);
	}

	public ItemStack getDisplayIcon() {
		return this.displayIcon.clone();
	}

	public void save() {
		String path = "kits." + name;

		FileConfiguration configFile = HCFPractice.get().getKitsConfig().getConfiguration();
		configFile.set(path + ".enabled", enabled);
		configFile.set(path + ".rankedSlot", rankedSlot);
		configFile.set(path + ".unrankedSlot", unrankedSlot);
		configFile.set(path + ".icon.material", displayIcon.getType().name());
		configFile.set(path + ".icon.durability", displayIcon.getDurability());

		configFile.set(path + ".loadout.armor", InventoryUtil.itemStackArrayToBase64(kitLoadout.getArmor()));
		configFile.set(path + ".loadout.contents", InventoryUtil.itemStackArrayToBase64(kitLoadout.getContents()));

		configFile.set(path + ".game-rules.allow-build", gameRules.isBuild());
		configFile.set(path + ".game-rules.spleef", gameRules.isSpleef());
		configFile.set(path + ".game-rules.parkour", gameRules.isParkour());
		configFile.set(path + ".game-rules.sumo", gameRules.isSumo());
		configFile.set(path + ".game-rules.bridge", gameRules.isBridge());
		configFile.set(path + ".game-rules.health-regeneration", gameRules.isHealthRegeneration());
		configFile.set(path + ".game-rules.show-health", gameRules.isShowHealth());
		configFile.set(path + ".game-rules.hit-delay", gameRules.getHitDelay());
		configFile.set(path + ".game-rules.ranked", gameRules.isRanked());
		configFile.set(path + ".game-rules.hcf", gameRules.isHcf());
		List<String> list = new ArrayList<>();
		for (PotionEffect potionEffect : gameRules.getEffects()) {
			list.add(PotionUtil.convertPotionEffectToString(potionEffect));
		}
		configFile.set(path + ".game-rules.effects", list);
		configFile.set(path + ".game-rules.boxing", gameRules.isBoxing());
		configFile.set(path + ".game-rules.kbprofile", gameRules.getKbProfile());
		configFile.set(path + ".game-rules.hcftrap", gameRules.isHcfTrap());
		configFile.set(path + ".game-rules.skywars", gameRules.isSkywars());
		configFile.set(path + ".game-rules.no-food", gameRules.isNoFood());
		configFile.set(path + ".game-rules.no-falldamage", gameRules.isNoFallDamage());
		configFile.set(path + ".game-rules.soup", gameRules.isSoup());
		configFile.set(path + ".edit-rules.editoritems", InventoryUtil.itemStackArrayToBase64(editRules.getEditorItems().toArray(new ItemStack[0])));
		configFile.set(path + ".edit-rules.allow-potion-fill", editRules.isAllowPotionFill());

		HCFPractice.get().getKitsConfig().save();
	}

	public void delete() {
		String path = "kits." + name;

		BasicConfigurationFile configFile = HCFPractice.get().getKitsConfig();
		configFile.getConfiguration().set(path, null);

		configFile.save();
		configFile.reload();

		kits.remove(this);
	}

	public static void init() {
		FileConfiguration config = HCFPractice.get().getKitsConfig().getConfiguration();

		for (String key : config.getConfigurationSection("kits").getKeys(false)) {
			String path = "kits." + key;

			Kit kit = new Kit(key);
			kit.setEnabled(config.getBoolean(path + ".enabled"));

			kit.setRankedSlot(config.getInt(path + ".rankedSlot"));
			kit.setUnrankedSlot(config.getInt(path + ".unrankedSlot"));

			kit.setDisplayIcon(new ItemBuilder(Material.valueOf(config.getString(path + ".icon.material")))
					.durability(config.getInt(path + ".icon.durability"))
					.build());

			if (config.contains(path + ".loadout.armor")) {
				try {
					kit.getKitLoadout().setArmor(InventoryUtil.itemStackArrayFromBase64(config.getString(path + ".loadout.armor")));
				} catch (IOException ignore) {
					throw new IllegalArgumentException("Error on try load loadout of kit " + kit.getName());
				}
			}

			if (config.contains(path + ".loadout.contents")) {
				try {
					kit.getKitLoadout().setContents(InventoryUtil.itemStackArrayFromBase64(config.getString(path + ".loadout.contents")));
				} catch (IOException ignore) {
					throw new IllegalArgumentException("Error on try load loadout of kit " + kit.getName());
				}
			}

			kit.getGameRules().setBuild(config.getBoolean(path + ".game-rules.allow-build"));
			kit.getGameRules().setSpleef(config.getBoolean(path + ".game-rules.spleef"));
			kit.getGameRules().setParkour(config.getBoolean(path + ".game-rules.parkour"));
			kit.getGameRules().setSumo(config.getBoolean(path + ".game-rules.sumo"));
			kit.getGameRules().setBridge(config.getBoolean(path + ".game-rules.bridge"));
			kit.getGameRules().setHealthRegeneration(config.getBoolean(path + ".game-rules.health-regeneration"));
			kit.getGameRules().setShowHealth(config.getBoolean(path + ".game-rules.show-health"));
			kit.getGameRules().setHitDelay(config.getInt(path + ".game-rules.hit-delay"));
			kit.getGameRules().setRanked(config.getBoolean(path + ".game-rules.ranked"));
			kit.getGameRules().setHcf(config.getBoolean(path + ".game-rules.hcf"));
			kit.getGameRules().setBoxing(config.getBoolean(path + ".game-rules.boxing"));
			List<PotionEffect> list = new ArrayList<>();
			for (String s : config.getStringList(path + ".game-rules.effects")) {
				list.add(PotionUtil.convertStringToPotionEffect(s));
			}
			for (PotionEffect potionEffect : list)
				kit.getGameRules().getEffects().add(potionEffect);
			kit.getGameRules().setKbProfile(config.getString(path + ".game-rules.kbprofile"));
			kit.getGameRules().setHcfTrap(config.getBoolean(path + ".game-rules.hcftrap"));
			kit.getGameRules().setSkywars(config.getBoolean(path + ".game-rules.skywars"));
			kit.getGameRules().setNoFood(config.getBoolean(path + ".game-rules.no-food"));
			kit.getGameRules().setNoFallDamage(config.getBoolean(path + ".game-rules.no-falldamage"));
			kit.getGameRules().setSoup(config.getBoolean(path + ".game-rules.soup"));
			if (config.contains(path + ".edit-rules.editoritems")) {
				try {
					for (ItemStack configItem : InventoryUtil.itemStackArrayFromBase64(config.getString(path + ".edit-rules.editoritems"))) {
						kit.getEditRules().getEditorItems().add(configItem);
					}
				} catch (IOException e) {
					throw new IllegalArgumentException("Error on try load editoritems (" + kit.getName() + ")");
				}
			}
			kit.getEditRules().setAllowPotionFill(config.getBoolean(".edit-rules.allow-potion-fill"));

			if (config.getConfigurationSection(path + ".edit-rules.items") != null) {
				for (String itemKey : config.getConfigurationSection(path + ".edit-rules.items").getKeys(false)) {
					kit.getEditRules().getEditorItems().add(new ItemBuilder(Material.valueOf(
							config.getString(path + ".edit-rules.items." + itemKey + ".material")))
							.durability(config.getInt(path + ".edit-rules.items." + itemKey + ".durability"))
							.amount(config.getInt(path + ".edit-rules.items." + itemKey + ".amount"))
							.build());
				}
			}

			kits.add(kit);
		}

		for (Kit kit : kits) {
			if (kit.isEnabled()) {
				new Queue(kit, false);
				if (kit.getGameRules().isRanked()) {
					new Queue(kit, true);
				}
			}
		}
	}

	public void rename(String newName) {
		String path = "kits." + name;
        String newPath = "kits." + newName;

        BasicConfigurationFile configFile = HCFPractice.get().getKitsConfig();
        configFile.getConfiguration().set(newPath, configFile.getConfiguration().getConfigurationSection(path));
        configFile.getConfiguration().set(path, null);

        configFile.save();
        configFile.reload();

        name = newName;
	}

	public static Kit getByName(String name) {
		for (Kit kit : kits) {
			if (kit.getName().equalsIgnoreCase(name)) {
				return kit;
			}
		}

		return null;
	}

	public boolean isStandaloneType() {
		return gameRules.isBuild() || gameRules.isHcfTrap() || gameRules.isSkywars() || gameRules.isBridge()
				|| gameRules.isSpleef();
	}
}
