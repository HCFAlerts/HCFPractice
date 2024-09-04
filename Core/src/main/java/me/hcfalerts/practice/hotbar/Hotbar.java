package me.hcfalerts.practice.hotbar;

import lombok.Getter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameState;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.PlayerUtil;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Hotbar {

	@Getter private static final Map<HotbarItem, HotbarEntry> items = new HashMap<>();

	public static void init() {
		FileConfiguration config = HCFPractice.get().getHotbarConfig().getConfiguration();

		for (HotbarItem hotbarItem : HotbarItem.values()) {
			try {
				String path = "HOTBAR_ITEMS." + hotbarItem.name() + ".";
				int slot = config.getInt(path + "SLOT");
				boolean enabled = config.getBoolean(path + "ENABLED");

				ItemBuilder builder = new ItemBuilder(Material.valueOf(config.getString(path + "MATERIAL")));
				builder.durability(config.getInt(path + "DURABILITY"));
				builder.name(config.getString(path + "NAME"));
				builder.lore(config.getStringList(path + "LORE"));

				HotbarEntry hotbarEntry;
				if (slot > 0) hotbarEntry = new HotbarEntry(builder.build(), slot - 1);
				else hotbarEntry = new HotbarEntry(builder.build(), -1);

				hotbarItem.setEnabled(enabled);

				items.put(hotbarItem, hotbarEntry);
			} catch (Exception e) {
				System.out.print("Failed to parse item " + hotbarItem.name());
			}
		}

		Map<HotbarItem, String> dynamicContent = new HashMap<>();
		dynamicContent.put(HotbarItem.MAP_SELECTION, "%MAP%");
		dynamicContent.put(HotbarItem.KIT_SELECTION, "%KIT%");

		for (Map.Entry<HotbarItem, String> entry : dynamicContent.entrySet()) {
			String voteName = Hotbar.getItems().get(entry.getKey()).getItemStack().getItemMeta().getDisplayName();
			String[] nameSplit = voteName.split(entry.getValue());

			entry.getKey().setPattern(
					Pattern.compile("(" + nameSplit[0] + ")(.*)(" + (nameSplit.length > 1 ? nameSplit[1] : "") + ")"));
		}
	}

	public static void giveHotbarItems(Player player) {
		Profile profile = Profile.get(player.getUniqueId());

		ItemStack[] itemStacks = new ItemStack[9];
		Arrays.fill(itemStacks, null);

		boolean activeRematch = profile.getRematchData() != null;
		boolean activeEvent = EventGame.getActiveGame() != null &&
		                      EventGame.getActiveGame().getGameState() == EventGameState.WAITING_FOR_PLAYERS;

		switch (profile.getState()) {
			case LOBBY: {
				if (profile.getKitEditorStatus() == null) {
					if (profile.getParty() == null) {
						if (HotbarItem.QUEUES_MENU.isEnabled()) itemStacks[getSlot(HotbarItem.QUEUES_MENU)] = getItem(HotbarItem.QUEUES_MENU);
						if (HotbarItem.QUEUE_JOIN_UNRANKED.isEnabled()) itemStacks[getSlot(HotbarItem.QUEUE_JOIN_UNRANKED)] = getItem(HotbarItem.QUEUE_JOIN_UNRANKED);
						if (HotbarItem.QUEUE_JOIN_RANKED.isEnabled()) itemStacks[getSlot(HotbarItem.QUEUE_JOIN_RANKED)] = getItem(HotbarItem.QUEUE_JOIN_RANKED);
						if (HotbarItem.KIT_EDITOR.isEnabled()) itemStacks[getSlot(HotbarItem.KIT_EDITOR)] = getItem(HotbarItem.KIT_EDITOR);

						if (activeRematch && activeEvent) {
							if (profile.getRematchData().isReceive())
								itemStacks[getSlot(HotbarItem.REMATCH_ACCEPT)] = getItem(HotbarItem.REMATCH_ACCEPT);
							else
								itemStacks[getSlot(HotbarItem.REMATCH_REQUEST)] = getItem(HotbarItem.REMATCH_REQUEST);

							if (HotbarItem.EVENT_JOIN.isEnabled()) itemStacks[getSlot(HotbarItem.EVENT_JOIN)] = getItem(HotbarItem.EVENT_JOIN);
							if (HotbarItem.PARTY_CREATE.isEnabled()) itemStacks[getSlot(HotbarItem.PARTY_CREATE)] = getItem(HotbarItem.PARTY_CREATE);
						} else if (activeRematch) {
							if (profile.getRematchData().isReceive())
								itemStacks[getSlot(HotbarItem.REMATCH_ACCEPT)] = getItem(HotbarItem.REMATCH_ACCEPT);
							else
								itemStacks[getSlot(HotbarItem.REMATCH_REQUEST)] = getItem(HotbarItem.REMATCH_REQUEST);

							if (HotbarItem.PARTY_CREATE.isEnabled()) itemStacks[getSlot(HotbarItem.PARTY_CREATE)] = getItem(HotbarItem.PARTY_CREATE);
						} else if (activeEvent) {
							if (HotbarItem.EVENT_JOIN.isEnabled()) itemStacks[getSlot(HotbarItem.EVENT_JOIN)] = getItem(HotbarItem.EVENT_JOIN);
							if (HotbarItem.PARTY_CREATE.isEnabled()) itemStacks[getSlot(HotbarItem.PARTY_CREATE)] = getItem(HotbarItem.PARTY_CREATE);
						} else {
							if (HotbarItem.PARTY_CREATE.isEnabled())
								itemStacks[getSlot(HotbarItem.PARTY_CREATE)] = getItem(HotbarItem.PARTY_CREATE);
						}

						if (HotbarItem.LEADERBOARD_MENU.isEnabled()) itemStacks[getSlot(HotbarItem.LEADERBOARD_MENU)] = getItem(HotbarItem.LEADERBOARD_MENU);
						if (HotbarItem.EVENT_SELECT.isEnabled()) itemStacks[getSlot(HotbarItem.EVENT_SELECT)] = getItem(HotbarItem.EVENT_SELECT);
					} else {
						if (profile.getParty().getLeader().getUniqueId().equals(profile.getUuid())) {
							if (HotbarItem.PARTY_EVENTS.isEnabled()) itemStacks[getSlot(HotbarItem.PARTY_EVENTS)] = getItem(HotbarItem.PARTY_EVENTS);
							if (profile.getParty().getListOfPlayers().size() >= HCFPractice.get().getMainConfig().getInteger("MATCH.MINIMUM_PLAYERS_TO_PARTY_TEAMFIGHT")
									&& HotbarItem.CLASS_SELECT.isEnabled()) {
								itemStacks[getSlot(HotbarItem.CLASS_SELECT)] = getItem(HotbarItem.CLASS_SELECT);
							}
							else {
								if (HotbarItem.CLASS_SELECT.isEnabled()) itemStacks[getSlot(HotbarItem.CLASS_SELECT)] = new ItemStack(Material.AIR);
							}
						}
						if (HotbarItem.PARTY_INFORMATION.isEnabled()) itemStacks[getSlot(HotbarItem.PARTY_INFORMATION)] = getItem(HotbarItem.PARTY_INFORMATION);
						if (HotbarItem.OTHER_PARTIES.isEnabled()) itemStacks[getSlot(HotbarItem.OTHER_PARTIES)] = getItem(HotbarItem.OTHER_PARTIES);
						if (HotbarItem.PARTY_DISBAND.isEnabled()) itemStacks[getSlot(HotbarItem.PARTY_DISBAND)] = getItem(HotbarItem.PARTY_DISBAND);
					}
				}
			}
			break;
			case QUEUEING: {
				if (HotbarItem.QUEUE_LEAVE.isEnabled()) itemStacks[getSlot(HotbarItem.QUEUE_LEAVE)] = getItem(HotbarItem.QUEUE_LEAVE);
			}
			break;
			case SPECTATING:
			case FIGHTING: {
				if (HotbarItem.SPECTATE_STOP.isEnabled()) itemStacks[getSlot(HotbarItem.SPECTATE_STOP)] = getItem(HotbarItem.SPECTATE_STOP);
			}
			break;
			case EVENT: {
				if (HotbarItem.EVENT_LEAVE.isEnabled()) itemStacks[getSlot(HotbarItem.EVENT_LEAVE)] = getItem(HotbarItem.EVENT_LEAVE);
			}
			break;
			case STAFF_MODE: {
				if (HotbarItem.RANDOM_TELEPORT.isEnabled()) itemStacks[getSlot(HotbarItem.RANDOM_TELEPORT)] = getItem(HotbarItem.RANDOM_TELEPORT);
				if (HotbarItem.HIDE_ALL_PLAYERS.isEnabled()) itemStacks[getSlot(HotbarItem.HIDE_ALL_PLAYERS)] = getItem(HotbarItem.HIDE_ALL_PLAYERS);
				if (HotbarItem.RESET.isEnabled()) itemStacks[getSlot(HotbarItem.RESET)] = getItem(HotbarItem.RESET);
			}
			break;
		}

		PlayerUtil.reset(player);

		for (int i = 0; i < 9; i++) {
			player.getInventory().setItem(i, itemStacks[i]);
		}
		
		player.updateInventory();
	}

	public static HotbarItem fromItemStack(ItemStack itemStack) {
		for (Map.Entry<HotbarItem, HotbarEntry> entry : Hotbar.getItems().entrySet()) {
			if (entry.getValue() != null && entry.getValue().getItemStack().equals(itemStack)) {
				return entry.getKey();
			}
		}

		return null;
	}

	public static ItemStack getItem(HotbarItem hotbarItem) {
		if (items.get(hotbarItem) != null)
			return items.get(hotbarItem).getItemStack();
		return null;
	}

	public static int getSlot(HotbarItem hotbarItem) {
		return items.get(hotbarItem).getSlot();
	}

}
