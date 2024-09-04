package me.hcfalerts.practice.party.menu;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.impl.BasicFreeForAllMatch;
import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.match.impl.BasicTeamRoundMatch;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.party.PartyEvent;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.TeamGameParticipant;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class PartyEventSelectKitMenu extends Menu {

	private PartyEvent partyEvent;

	@Override
	public String getTitle(Player player) {
		return "&8Select a Kit";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		Map<Integer, Button> buttons = new HashMap<>();

		for (Kit kit : Kit.getKits()) {
			if (kit.isEnabled()) {
				if (partyEvent == PartyEvent.FFA) {
					if (!(kit.getGameRules().isBoxing() || kit.getGameRules().isHcfTrap() || kit.getGameRules().isSkywars() || kit.getGameRules().isBridge())) {
						buttons.put(buttons.size(), new SelectKitButton(partyEvent, kit));
					}
				}
				else if (partyEvent == PartyEvent.SPLIT) {
					if (!(kit.getGameRules().isBoxing() || kit.getGameRules().isBridge())) {
						buttons.put(buttons.size(), new SelectKitButton(partyEvent, kit));
					}
				}
			}
		}

		return buttons;
	}

	@RequiredArgsConstructor
	private static class SelectKitButton extends Button {

		private final PartyEvent partyEvent;
		private final Kit kit;

		@Override
		public ItemStack getButtonItem(Player player) {
			return new ItemBuilder(kit.getDisplayIcon())
					.addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
					.addItemFlag(ItemFlag.HIDE_ENCHANTS)
					.addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
					.name("&a" + kit.getName())
					.build();
		}

		@Override
		public void clicked(Player player, ClickType clickType) {
			Menu.currentlyOpenedMenus.get(player.getName()).setClosedByMenu(true);

			player.closeInventory();

			Profile profile = Profile.get(player.getUniqueId());

			if (profile.getParty() == null) {
				new MessageFormat(me.hcfalerts.practice.Locale.PARTY_NOT_IN_A_PARTY
						.format(profile.getLocale()))
						.send(player);
				return;
			}

			if (profile.getParty().getPlayers().size() <= 1) {
				player.sendMessage(CC.RED + "You do not have enough players in your party to start an event.");
				return;
			}

			Party party = profile.getParty();
			Arena arena = Arena.getRandomArena(kit);

			if (arena == null) {
				new MessageFormat(Locale.DUEL_NO_ARENAS_AVAILABLE
						.format(profile.getLocale()))
						.send(player);
				return;
			}

			arena.setBusy(true);

			Match match;

			if (partyEvent == PartyEvent.FFA) {
				List<GameParticipant<MatchGamePlayer>> participants = new ArrayList<>();

				for (Player partyPlayer : party.getListOfPlayers()) {
					participants.add(new GameParticipant<>(
							new MatchGamePlayer(partyPlayer.getUniqueId(), partyPlayer.getName())));
				}

				match = new BasicFreeForAllMatch(null, kit, arena, participants);
			} else {
				Player partyLeader = party.getLeader();
				Player randomLeader = Bukkit.getPlayer(party.getPlayers().get(1));

				MatchGamePlayer leaderA = new MatchGamePlayer(partyLeader.getUniqueId(), partyLeader.getName());
				MatchGamePlayer leaderB = new MatchGamePlayer(randomLeader.getUniqueId(), randomLeader.getName());

				GameParticipant<MatchGamePlayer> participantA = new TeamGameParticipant<>(leaderA);
				GameParticipant<MatchGamePlayer> participantB = new TeamGameParticipant<>(leaderB);

				List<Player> players = new ArrayList<>(party.getListOfPlayers());
				Collections.shuffle(players);

				for (Player otherPlayer : players) {
					if (participantA.containsPlayer(otherPlayer.getUniqueId()) ||
					    participantB.containsPlayer(otherPlayer.getUniqueId())) {
						continue;
					}

					MatchGamePlayer gamePlayer = new MatchGamePlayer(otherPlayer.getUniqueId(), otherPlayer.getName());

					if (participantA.getPlayers().size() > participantB.getPlayers().size()) {
						participantB.getPlayers().add(gamePlayer);
					} else {
						participantA.getPlayers().add(gamePlayer);
					}
				}

				// Create match
				if (kit.getGameRules().isBridge()) match = new BasicTeamRoundMatch(null, kit, arena, false, participantA, participantB, 3);
				else match = new BasicTeamMatch(null, kit, arena, false, participantA, participantB);
			}

			// Start match
			match.start();
		}

	}

}
