package me.hcfalerts.practice.party.menu;

import lombok.AllArgsConstructor;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OtherPartiesMenu extends PaginatedMenu {

	@Override
	public String getPrePaginatedTitle(Player player) {
		return "&8Other Parties";
	}

	@Override
	public Map<Integer, Button> getAllPagesButtons(Player player) {
		Profile profile = Profile.get(player.getUniqueId());

		Map<Integer, Button> buttons = new HashMap<>();

		for (Party party : Party.getParties()) {
			if (!party.equals(profile.getParty())) {
				buttons.put(buttons.size(), new PartyDisplayButton(party));
			}
		}

		return buttons;
	}

	@AllArgsConstructor
	public static class PartyDisplayButton extends Button {

		private Party party;

		@Override
		public ItemStack getButtonItem(Player player) {
			List<String> lore = new ArrayList<>();
			int added = 0;

			for (Player partyPlayer : party.getListOfPlayers()) {
				if (added >= 10) {
					break;
				}

				lore.add(CC.GRAY + " - " + CC.RESET + partyPlayer.getPlayer().getName());

				added++;
			}

			if (party.getPlayers().size() != added) {
				lore.add(CC.GRAY + " and " + (party.getPlayers().size() - added) + " others...");
			}

			return new ItemBuilder(Material.SKULL_ITEM)
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.name("&dParty of &r" + party.getLeader().getName())
					.amount(party.getPlayers().size())
					.durability(3)
					.lore(lore)
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getParty() != null) {
				if (!profile.getParty().equals(party)) {
					if (profile.getParty().getLeader().equals(player)) {
						player.chat("/duel " + party.getLeader().getName());
					} else {
						player.sendMessage(ChatColor.RED + "You are not the leader of your party.");
					}
				}
			}
		}

	}
}
