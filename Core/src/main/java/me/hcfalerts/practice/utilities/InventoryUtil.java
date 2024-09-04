package me.hcfalerts.practice.utilities;

import com.google.common.collect.Lists;
import me.hcfalerts.practice.HCFPractice;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class InventoryUtil {

	public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(items.length);
			for (ItemStack item : items) {
				dataOutput.writeObject(item);
			}
			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception e) {
			throw new IllegalStateException("Unable to save item stacks.", e);
		}
	}

	public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];
			for (int i = 0; i < items.length; ++i) {
				items[i] = (ItemStack) dataInput.readObject();
			}
			dataInput.close();
			return items;
		} catch (ClassNotFoundException e) {
			throw new IOException("Unable to decode class type.", e);
		}
	}

	public static void saveItemStacksKit(String path, ItemStack[] source, boolean armor) {
		for (int b = 0; b < source.length; b++) {
			if (armor) {
				HCFPractice.get().getKitsConfig().getConfiguration().set(path + ".loadout.armor." + b, Arrays.asList(source).get(b));
			} else {
				HCFPractice.get().getKitsConfig().getConfiguration().set(path + ".loadout.contents." + b, Arrays.asList(source).get(b));
			}
		}
	}

	public static ItemStack[] serializeItemStackKit(String source) {
		List<ItemStack> items = Lists.newArrayList();

		for (String key : HCFPractice.get().getKitsConfig().getConfiguration().getConfigurationSection(source).getKeys(false)) {
			items.add(HCFPractice.get().getKitsConfig().getConfiguration().getItemStack(source + "." + key));
		}

		return items.toArray(new ItemStack[items.size()]);
	}

	public static ItemStack[] fixInventoryOrder(ItemStack[] source) {
		ItemStack[] fixed = new ItemStack[36];

		System.arraycopy(source, 0, fixed, 27, 9);
		System.arraycopy(source, 9, fixed, 0, 27);

		return fixed;
	}

	public static void removeCrafting(Material material) {
		Iterator<Recipe> iterator = HCFPractice.get().getServer().recipeIterator();

		while (iterator.hasNext()) {
			Recipe recipe = iterator.next();

			if (recipe != null && recipe.getResult().getType() == material) {
				iterator.remove();
			}
		}
	}

	public static ItemStack[] leatherArmor(Color color){
		return new ItemStack[]{
				new ItemBuilder(Material.LEATHER_BOOTS).color(color).build(),
				new ItemBuilder(Material.LEATHER_LEGGINGS).color(color).build(),
				new ItemBuilder(Material.LEATHER_CHESTPLATE).color(color).build(),
				new ItemBuilder(Material.LEATHER_HELMET).color(color).build()
		};
	}

}