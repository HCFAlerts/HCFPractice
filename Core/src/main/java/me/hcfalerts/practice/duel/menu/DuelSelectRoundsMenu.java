package me.hcfalerts.practice.duel.menu;

import lombok.AllArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.duel.DuelProcedure;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DuelSelectRoundsMenu extends Menu {

	public DuelSelectRoundsMenu() {
		super();
		setUpdateAfterClick(true);
	}

	@Override
	public String getTitle(Player player) {
		return HCFPractice.get().getLangConfig().getString("DUEL.SELECT.ROUNDS_MENU.TITLE");
	}

	@Override
	public int getSize() {
		return 9*3;
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		Profile profile = Profile.get(player.getUniqueId());
		DuelProcedure duelProcedure = profile.getDuelProcedure();

		buttons.put(11, new DecreaseRoundsButton(duelProcedure));
		buttons.put(13, new DuelInformationButton());
		buttons.put(15, new IncreaseRoundsButton(duelProcedure));
		buttons.put(26, new ConfirmButton(duelProcedure));

		return buttons;
	}

	@Override
	public void onClose(Player player) {
		if (!isClosedByMenu()) {
			Profile profile = Profile.get(player.getUniqueId());
			profile.setDuelProcedure(null);
		}
	}

	@AllArgsConstructor
	private static class DuelInformationButton extends Button {

		@Override
		public ItemStack getButtonItem(Player player) {
			DuelProcedure duelProcedure = Profile.get(player.getUniqueId()).getDuelProcedure();
			List<String> lore = HCFPractice.get().getLangConfig().getStringList("DUEL.SELECT.ROUNDS_MENU.DUEL_INFORMATION.LORE_FORMAT");

			lore.replaceAll(s -> s
					.replace("{kit}", duelProcedure.getKit().getName())
					.replace("{arena}", duelProcedure.getArena().getName())
					.replace("{rounds}", String.valueOf(duelProcedure.getRounds()))
					.replace("{rival}", duelProcedure.getTarget() == null ? "This player is Disconnect!" : duelProcedure.getTarget().getName()));

			return new ItemBuilder(duelProcedure.getKit().getDisplayIcon())
					.name(CC.GREEN + duelProcedure.getKit().getName())
					.lore(lore)
					.build();
		}
	}

	@AllArgsConstructor
	private static class IncreaseRoundsButton extends Button {

		private final DuelProcedure duelProcedure;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_GLASS)
					.durability(5)
					.name(HCFPractice.get().getLangConfig().getString("DUEL.SELECT.ROUNDS_MENU.INCREASE_ROUNDS_TITLE"))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.get(player.getUniqueId());

			// Update and request the procedure
			profile.getDuelProcedure().setRounds(duelProcedure.getRounds() + 1);

			// Update message
			player.sendMessage(CC.GREEN + "You have increased the rounds to " + CC.YELLOW + duelProcedure.getRounds());
		}
	}

	@AllArgsConstructor
	private static class DecreaseRoundsButton extends Button {

		private final DuelProcedure duelProcedure;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.STAINED_GLASS)
					.durability(14)
					.name(HCFPractice.get().getLangConfig().getString("DUEL.SELECT.ROUNDS_MENU.DECREASE_ROUNDS_TITLE"))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.get(player.getUniqueId());

			// Update and request the procedure
			profile.getDuelProcedure().setRounds(duelProcedure.getRounds() - 1);

			// Update message
			player.sendMessage(CC.GREEN + "You have decreased the rounds to " + CC.YELLOW + duelProcedure.getRounds());
		}
	}

	@AllArgsConstructor
	private static class ConfirmButton extends Button {

		private final DuelProcedure duelProcedure;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(Material.valueOf(HCFPractice.get().getLangConfig().getString("DUEL.SELECT.ROUNDS_MENU.CONFIRM_BUTTON.MATERIAL")))
					.durability(HCFPractice.get().getLangConfig().getInteger("DUEL.SELECT.ROUNDS_MENU.CONFIRM_BUTTON.DATA"))
					.name(HCFPractice.get().getLangConfig().getString("DUEL.SELECT.ROUNDS_MENU.CONFIRM_BUTTON.NAME"))
					.lore(HCFPractice.get().getLangConfig().getStringList("DUEL.SELECT.ROUNDS_MENU.CONFIRM_BUTTON.LORE"))
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.get(player.getUniqueId());

			// Send duel request
			profile.getDuelProcedure().send();

			// Set closed by menu
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

			// Force close inventory
			player.closeInventory();
		}
	}
}
