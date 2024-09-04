package me.hcfalerts.practice.kit.menu.edit;

import lombok.AllArgsConstructor;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitLoadout;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.menu.button.BackButton;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KitManagementMenu extends Menu {

	private static final Button PLACEHOLDER = Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 7, " ");

	private final Kit kit;

	public KitManagementMenu(Kit kit) {
		this.kit = kit;

		setPlaceholder(true);
		setUpdateAfterClick(false);
	}

	@Override
	public String getTitle(Player player) {
		return "&8Viewing " + kit.getName() + " Kits";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		Profile profile = Profile.get(player.getUniqueId());
		KitLoadout[] kitLoadouts = profile.getKitData().get(kit).getLoadouts();

		if (kitLoadouts == null) {
			return buttons;
		}

		int startPos = -1;

		for (int i = 0; i < 4; i++) {
			startPos += 2;

			KitLoadout kitLoadout = kitLoadouts[i];
			buttons.put(startPos, kitLoadout == null ? new CreateKitButton(i) : new KitDisplayButton(kitLoadout));
			buttons.put(startPos + 18, new LoadKitButton(i));
			buttons.put(startPos + 27, kitLoadout == null ? PLACEHOLDER : new RenameKitButton(kit, kitLoadout));
			buttons.put(startPos + 36, kitLoadout == null ? PLACEHOLDER : new DeleteKitButton(kit, kitLoadout));
		}

		buttons.put(36, new BackButton(new KitEditorSelectKitMenu()));

		return buttons;
	}

	@Override
	public void onClose(Player player) {
		if (!isClosedByMenu()) {
			Profile profile = Profile.get(player.getUniqueId());
			profile.getKitEditorData().setSelectedKit(null);
		}
	}

	@AllArgsConstructor
	private class DeleteKitButton extends Button { // TODO: No funciona

		private Kit kit;
		private KitLoadout kitLoadout;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_CLAY)
					.name("&cDelete")
					.durability(14)
					.lore(Arrays.asList(
							"&cClick to delete this kit.",
							"&cYou will &lNOT &cbe able to",
							"&crecover this kitloadout."
					))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.get(player.getUniqueId());
			profile.getKitData().get(kit).deleteKit(kitLoadout);

			new KitManagementMenu(kit).openMenu(player);
		}

	}

	@AllArgsConstructor
	private class CreateKitButton extends Button { // TODO: No funciona

		private int index;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.IRON_SWORD)
					.name("&aCreate Kit")
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.get(player.getUniqueId());
			Kit kit = profile.getKitEditorData().getSelectedKit();

			// TODO: this shouldn't be null but sometimes it is?
			if (kit == null) {
				player.closeInventory();
				return;
			}

			KitLoadout kitLoadout = new KitLoadout("Kit " + (index + 1));

			if (kit.getKitLoadout() != null) {
				if (kit.getKitLoadout().getArmor() != null) {
					kitLoadout.setArmor(kit.getKitLoadout().getArmor());
				}

				if (kit.getKitLoadout().getContents() != null) {
					kitLoadout.setContents(kit.getKitLoadout().getContents());
				}
			}

			profile.getKitData().get(kit).replaceKit(index, kitLoadout);
			profile.getKitEditorData().setSelectedKitLoadout(kitLoadout);

			new KitEditorMenu(index).openMenu(player);
		}

	}

	@AllArgsConstructor
	private class RenameKitButton extends Button { // TODO: No funciona

		private Kit kit;
		private KitLoadout kitLoadout;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.SIGN)
					.name("&eRename")
					.lore("&eClick to rename this kit.")
					.build();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);
			Profile profile = Profile.get(player.getUniqueId());

			player.closeInventory();
			//player.sendMessage(Locale.KIT_EDITOR_START_RENAMING.format(kitLoadout.getCustomName()));

			new MessageFormat(Locale.KIT_EDITOR_START_RENAMING
				.format(profile.getLocale()))
				.add("{kit_name}", kitLoadout.getCustomName())
				.send(player);

			profile.getKitEditorData().setSelectedKit(kit);
			profile.getKitEditorData().setSelectedKitLoadout(kitLoadout);
			profile.getKitEditorData().setActive(true);
			profile.getKitEditorData().setRename(true);
		}

	}

	@AllArgsConstructor
	private class LoadKitButton extends Button { // TODO: No funciona

		private int index;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.BOOK)
					.name("&aLoad/Edit")
					.lore("&eClick to edit this kit.")
					.build();
		}

		@Override
		public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
			Profile profile = Profile.get(player.getUniqueId());

			// TODO: this shouldn't be null but sometimes it is?
			if (profile.getKitEditorData().getSelectedKit() == null) {
				player.closeInventory();
				return;
			}

			KitLoadout kit = profile.getKitData().get(profile.getKitEditorData().getSelectedKit()).getLoadout(index);

			if (kit == null) {
				kit = new KitLoadout("Kit " + (index + 1));
				kit.setArmor(profile.getKitEditorData().getSelectedKit().getKitLoadout().getArmor());
				kit.setContents(profile.getKitEditorData().getSelectedKit().getKitLoadout().getContents());
				profile.getKitData().get(profile.getKitEditorData().getSelectedKit()).replaceKit(index, kit);
			}

			profile.getKitEditorData().setSelectedKitLoadout(kit);

			new KitEditorMenu(index).openMenu(player);
		}

	}

	@AllArgsConstructor
	private class KitDisplayButton extends Button { // TODO: No funciona

		private KitLoadout kitLoadout;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.BOOK)
					.name("&a" + kitLoadout.getCustomName())
					.build();
		}

	}

}
