package me.hcfalerts.practice.party.menu;

import lombok.AllArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.party.PartyEvent;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartyEventSelectEventMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&8Select an Event";
	}

	@Override
	public int getSize() {
		return 9*3;
	}


	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();
		buttons.put(11, new SelectEventButton(PartyEvent.FFA));
		buttons.put(15, new SelectEventButton(PartyEvent.SPLIT));
		buttons.put(13, new SelectEventButton(PartyEvent.HCFTEAMFIGHT));
		return buttons;
	}

	@AllArgsConstructor
	private static class SelectEventButton extends Button {

		private final PartyEvent partyEvent;

		@Override
		public ItemStack getButtonItem(Player player) {
			BasicConfigurationFile config = HCFPractice.get().getLangConfig();
			if (partyEvent == PartyEvent.FFA) {
				List<String> lore = config.getStringList("PARTY.FFA.LORE");
				lore.replaceAll(s -> s
						.replace("{name}", partyEvent.getName())
						.replace("{bars}", CC.SB_BAR));
				return new ItemBuilder(Material.DIAMOND_SWORD)
						.name(config.getString("PARTY.FFA.NAME")
								.replace("{name}", partyEvent.getName()))
						.lore(lore)
						.build();
			}

			else if (partyEvent == PartyEvent.SPLIT) {
			List<String> lore = config.getStringList("PARTY.SPLIT.LORE");
			lore.replaceAll(s -> s
					.replace("{name}", partyEvent.getName())
					.replace("{bars}", CC.SB_BAR));
			return new ItemBuilder(Material.GOLD_AXE)
					.name(config.getString("PARTY.SPLIT.NAME")
							.replace("{name}", partyEvent.getName()))
					.lore(lore)
					.build();
			}

		List<String> lore = config.getStringList("PARTY.HCFTEAMFIGHT.LORE");
			lore.replaceAll(s -> s
				.replace("{name}", partyEvent.getName())
				.replace("{bars}", CC.SB_BAR));
			return new ItemBuilder(Material.DIAMOND_CHESTPLATE)
					.name(config.getString("PARTY.HCFTEAMFIGHT.NAME")
							.replace("{name}", partyEvent.getName()))
				    .lore(lore)
					.build();

		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getParty() == null) {
				new MessageFormat(Locale.PARTY_NOT_IN_A_PARTY
						.format(profile.getLocale()))
						.send(player);
				return;
			}

			for (Player member : profile.getParty().getListOfPlayers()) {
				Profile profileMember = Profile.get(member.getUniqueId());
				if (profileMember.getState() != ProfileState.LOBBY) {
					new MessageFormat(Locale.PARTY_REQUIRED_ALL_PLAYERS_ON_LOBBY
							.format(profileMember.getLocale()))
							.send(player);
					player.closeInventory();
					return;
				}
			}

			new PartyEventSelectKitMenu(partyEvent).openMenu(player);
		}

	}

}
