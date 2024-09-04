package me.hcfalerts.practice.kit.menu.edit;

import lombok.AllArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitLoadout;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.meta.ProfileKitData;
import me.hcfalerts.practice.utilities.BukkitReflection;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.PlayerUtil;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.menu.button.DisplayButton;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class KitEditorMenu extends Menu {

	private static final int[] ITEM_POSITIONS = new int[]{
			20, 21, 22, 23, 24, 25, 26, 29, 30, 31, 32, 33, 34, 35, 38, 39, 40, 41, 42, 43, 44, 47, 48, 49, 50, 51, 52,
			53
	};
	private static final int[] BORDER_POSITIONS = new int[]{ 1, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 28, 37, 46 };
	private static final Button BORDER_BUTTON = Button.placeholder(Material.COAL_BLOCK, (byte) 0, " ");

	private final int index;

	{
		setUpdateAfterClick(false);
	}

	@Override
	public String getTitle(Player player) {
		Profile profile = Profile.get(player.getUniqueId());
		return "&8Editing &7»&8 " + profile.getKitEditorData().getSelectedKit().getName();
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (int border : BORDER_POSITIONS) {
			buttons.put(border, BORDER_BUTTON);
		}

		buttons.put(0, new CurrentKitButton());
		buttons.put(2, new SaveButton());
		buttons.put(6, new LoadDefaultKitButton());
		buttons.put(7, new ClearInventoryButton());
		buttons.put(8, new CancelButton(index));

		Profile profile = Profile.get(player.getUniqueId());
		Kit kit = profile.getKitEditorData().getSelectedKit();
		KitLoadout kitLoadout = profile.getKitEditorData().getSelectedKitLoadout();

		buttons.put(18, new ArmorDisplayButton(kitLoadout.getArmor()[3]));
		buttons.put(27, new ArmorDisplayButton(kitLoadout.getArmor()[2]));
		buttons.put(36, new ArmorDisplayButton(kitLoadout.getArmor()[1]));
		buttons.put(45, new ArmorDisplayButton(kitLoadout.getArmor()[0]));

		List<ItemStack> items = kit.getEditRules().getEditorItems();

		if (!kit.getEditRules().getEditorItems().isEmpty()) {
			for (int i = 0; i < kit.getEditRules().getEditorItems().size(); i++) {
				buttons.put(ITEM_POSITIONS[i], new InfiniteItemButton(items.get(i)));
			}
		}

		return buttons;
	}

	@Override
	public void onOpen(Player player) {
		if (!isClosedByMenu()) {
			TaskUtil.run(() -> PlayerUtil.reset(player));

			Profile profile = Profile.get(player.getUniqueId());
			profile.getKitEditorData().setActive(true);

			if (profile.getKitEditorData().getSelectedKit() != null) {
				player.getInventory().setContents(profile.getKitEditorData().getSelectedKitLoadout().getContents());
			}

			player.updateInventory();
		}
	}

	@Override
	public void onClose(Player player) {
		Profile profile = Profile.get(player.getUniqueId());
		profile.getKitEditorData().setActive(false);

		if (profile.getState() != ProfileState.FIGHTING) {
			new BukkitRunnable() {
				@Override
				public void run() {
					Hotbar.giveHotbarItems(player);
				}
			}.runTask(HCFPractice.get());
		}
	}

	@AllArgsConstructor
	private static class ArmorDisplayButton extends Button {

		private ItemStack itemStack;

		@Override
		public ItemStack getButtonItem(Player player) {
			if (itemStack == null || itemStack.getType() == Material.AIR) {
				return new ItemStack(Material.AIR);
			}

			return new ItemBuilder(itemStack.clone())
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.name(CC.AQUA + BukkitReflection.getItemStackName(itemStack))
					.lore(CC.YELLOW + "This is automatically equipped.")
					.build();
		}

	}

	@AllArgsConstructor
	private static class CurrentKitButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			Profile profile = Profile.get(player.getUniqueId());
			return new ItemBuilder(Material.NAME_TAG)
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.name("&8Editing &7»&8 " + profile.getKitEditorData().getSelectedKit().getName())
					.build();
		}

	}

	@AllArgsConstructor
	private static class ClearInventoryButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.durability(7)
					.name("&cClear Inventory")
					.lore(Arrays.asList(
							"&eThis will clear your inventory",
							"&eso you can start over."
					))
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);
			player.getInventory().setContents(new ItemStack[36]);
			player.updateInventory();
		}

		@Override
		public boolean shouldUpdate(Player player, ClickType clickType) {
			return true;
		}

	}

	@AllArgsConstructor
	private static class LoadDefaultKitButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.durability(7)
					.name(CC.YELLOW + CC.BOLD + "Load default kit")
					.lore(Arrays.asList(
							CC.YELLOW + "Click this to load the default kit",
							CC.YELLOW + "into the kit editing menu."
					))
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);

			Profile profile = Profile.get(player.getUniqueId());

			player.getInventory()
			      .setContents(profile.getKitEditorData().getSelectedKit().getKitLoadout().getContents());
			player.updateInventory();
		}

		@Override
		public boolean shouldUpdate(Player player, ClickType clickType) {
			return true;
		}

	}

	@AllArgsConstructor
	private static class SaveButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.durability(5)
					.name("&aSave")
					.lore("&eClick this to save your kit.")
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);
			player.closeInventory();

			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getKitEditorData().getSelectedKitLoadout() != null) {
				profile.getKitEditorData().getSelectedKitLoadout().setContents(player.getInventory().getContents());
			}

			Hotbar.giveHotbarItems(player);

			new KitManagementMenu(profile.getKitEditorData().getSelectedKit()).openMenu(player);
		}

	}

	@AllArgsConstructor
	private static class CancelButton extends Button {

		private int index;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.durability(14)
					.name("&cCancel")
					.lore(Arrays.asList(
							"&eClick this to abort editing your kit,",
							"&eand return to the kit menu."
					))
					.build();
		}

		@Override
		public void clicked(Player player, int i, ClickType clickType, int hb) {
			Button.playNeutral(player);

			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getKitEditorData().getSelectedKit() != null) {
				ProfileKitData kitData = profile.getKitData().get(profile.getKitEditorData().getSelectedKit());
				kitData.replaceKit(index, null);

				new KitManagementMenu(profile.getKitEditorData().getSelectedKit()).openMenu(player);
			}
		}

	}

	private static class InfiniteItemButton extends DisplayButton {

		InfiniteItemButton(ItemStack itemStack) {
			super(itemStack, false);
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbar) {
			Inventory inventory = player.getOpenInventory().getTopInventory();
			ItemStack itemStack = inventory.getItem(slot);

			inventory.setItem(slot, itemStack);

			player.setItemOnCursor(itemStack);
			player.updateInventory();
		}

	}

}
