package me.hcfalerts.practice.kit.menu.edit;

import lombok.AllArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class KitEditorSelectKitMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&8Select a Kit";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (Kit kit : Kit.getKits()) {
			if (kit.isEnabled()) {
				buttons.put(buttons.size(), new KitDisplayButton(kit));
			}
		}

		return buttons;
	}

	@AllArgsConstructor
	private class KitDisplayButton extends Button { // TODO: No funciona

		private Kit kit;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(kit.getDisplayIcon())
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.name("&d" + kit.getName())
					.lore(CC.SB_BAR)
					.lore("&7Click to Edit this Kit")
					.lore(CC.SB_BAR)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			player.closeInventory();

			Profile profile = Profile.get(player.getUniqueId());
			profile.getKitEditorData().setSelectedKit(kit);

			new KitManagementMenu(kit).openMenu(player);
		}

	}
}
