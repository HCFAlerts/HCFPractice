package me.hcfalerts.practice.duel;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.ChatComponentBuilder;
import me.hcfalerts.practice.utilities.chat.ChatHelper;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

@Getter @Setter
public class DuelProcedure {

	private final Player sender;
	private final Player target;
	private final boolean party;
	private Kit kit;
	private Arena arena;
	private int rounds;

	public DuelProcedure(Player sender, Player target, boolean party) {
		this.sender = sender;
		this.target = target;
		this.party = party;
		this.rounds = 1;
	}

	public void send() {
		Player target = this.target;

		if (!sender.isOnline() || target == null || !target.isOnline()) return;

		DuelRequest duelRequest = new DuelRequest(sender.getUniqueId(), target.getUniqueId(), party);
		duelRequest.setRounds(rounds);
		duelRequest.setKit(kit);
		duelRequest.setArena(arena);

		Profile senderProfile = Profile.get(sender.getUniqueId());
		senderProfile.setDuelProcedure(null);

		Profile targetProfile = Profile.get(target.getUniqueId());
		targetProfile.getDuelRequests().add(duelRequest);

		if (party) {
			new MessageFormat(Locale.DUEL_SENT_PARTY
				.format(senderProfile.getLocale()))
				.add("{kit_name}", kit.getName())
				.add("{target_name}", target.getName())
				.add("{arena_name}", arena.getName())
				.add("{party_size}", String.valueOf(targetProfile.getParty().getPlayers().size()))
				.send(sender);

			for (String msg : new MessageFormat(Locale.DUEL_RECEIVED_PARTY
								.format(targetProfile.getLocale()))
								.add("{kit_name}", kit.getName())
								.add("{sender_name}", sender.getName())
								.add("{arena_name}", arena.getName())
								.add("{party_size}", String.valueOf(targetProfile.getParty().getPlayers().size()))
								.toList()) {
				if (msg.contains("%CLICKABLE%")) {
					ChatComponentBuilder builder = new ChatComponentBuilder(new MessageFormat(Locale.DUEL_RECEIVED_CLICKABLE
						.format(targetProfile.getLocale()))
						.add("{sender_name}", sender.getName())
						.toString());
					builder.attachToEachPart(ChatHelper.click("/duel accept " + sender.getName()));
					builder.attachToEachPart(ChatHelper.hover(new MessageFormat(Locale.DUEL_RECEIVED_HOVER
						.format(targetProfile.getLocale()))
						.toString()));

					target.spigot().sendMessage(builder.create());
				} else {
					target.sendMessage(msg);
				}
			}
		} else {
			new MessageFormat(Locale.DUEL_SENT
				.format(senderProfile.getLocale()))
				.add("{kit_name}", kit.getName())
				.add("{target_name}", target.getName())
				.add("{arena_name}", arena.getName())
				.send(sender);

			for (String msg : new MessageFormat(Locale.DUEL_RECEIVED
								.format(targetProfile.getLocale()))
								.add("{kit_name}", kit.getName())
								.add("{sender_name}", sender.getName())
								.add("{arena_name}", arena.getName())
								.toList()) {
				if (msg.contains("%CLICKABLE%")) {
					ChatComponentBuilder builder = new ChatComponentBuilder(new MessageFormat(Locale.DUEL_RECEIVED_CLICKABLE
						.format(targetProfile.getLocale()))
						.add("{sender_name}", sender.getName())
						.toString());
					builder.attachToEachPart(ChatHelper.click("/duel accept " + sender.getName()));
					builder.attachToEachPart(ChatHelper.hover(new MessageFormat(Locale.DUEL_RECEIVED_HOVER
						.format(targetProfile.getLocale()))
						.toString()));

					target.spigot().sendMessage(builder.create());
				} else {
					target.sendMessage(msg);
				}
			}
		}
	}

}
