package me.hcfalerts.practice.duel.command;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.duel.DuelRequest;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.match.impl.BasicTeamRoundMatch;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.TeamGameParticipant;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DuelAcceptCommand extends BaseCommand {

	@Command(name = "duel.accept")
	@Override
	public void onCommand(CommandArgs commandArgs) {
		Player player = commandArgs.getPlayer();
		String[] args = commandArgs.getArgs();

		if (args.length == 0) {
			player.sendMessage(CC.RED + "Please insert a Player.");
			return;
		}

		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			new MessageFormat(Locale.PLAYER_NOT_FOUND.format(Profile.get(player.getUniqueId()).getLocale()))
					.send(player);
			return;
		}

		Profile playerProfile = Profile.get(player.getUniqueId());

		if (playerProfile.isBusy()) {
			new MessageFormat(Locale.DUEL_CANNOT_DUEL_RIGHT_NOW.format(playerProfile.getLocale()))
					.send(player);
			return;
		}

		Profile targetProfile = Profile.get(target.getUniqueId());

		if (targetProfile.isBusy()) {
			new MessageFormat(Locale.DUEL_IS_BUSY.format(playerProfile.getLocale()))
					.add("{player}", target.getName())
					.send(player);
			return;
		}

		DuelRequest duelRequest = playerProfile.getDuelRequest(target);

		if (duelRequest != null) {
			if (targetProfile.isDuelRequestExpired(duelRequest)) {
				new MessageFormat(Locale.DUEL_HAS_EXPIRED.format(playerProfile.getLocale()))
						.send(player);
				return;
			}

			if (duelRequest.isParty()) {
				if (playerProfile.getParty() == null) {
					new MessageFormat(Locale.DUEL_NOT_HAVE_PARTY.format(playerProfile.getLocale()))
							.send(player);
					return;
				}
				else if (targetProfile.getParty() == null) {
					new MessageFormat(Locale.DUEL_OTHER_NOT_HAVE_PARTY.format(playerProfile.getLocale()))
							.send(player);
					return;
				}
			} else {
				if (playerProfile.getParty() != null) {
					new MessageFormat(Locale.DUEL_IF_PARTY.format(playerProfile.getLocale()))
							.send(player);
					return;
				}
				else if (targetProfile.getParty() != null) {
					new MessageFormat(Locale.DUEL_IF_TARGET_IN_PARTY.format(playerProfile.getLocale()))
							.send(player);
					return;
				}
			}

			Arena arena = duelRequest.getArena();

			if (arena.isBusy()) arena = Arena.getRandomArena(duelRequest.getKit());

			if (arena == null) {
				new MessageFormat(Locale.DUEL_NO_ARENAS_AVAILABLE.format(playerProfile.getLocale()))
						.send(player);
				return;
			}

			playerProfile.getDuelRequests().remove(duelRequest);

			GameParticipant<MatchGamePlayer> participantA = null;
			GameParticipant<MatchGamePlayer> participantB = null;

			if (duelRequest.isParty()) {
				for (Party party : new Party[]{ playerProfile.getParty(), targetProfile.getParty() }) {
					Player leader = party.getLeader();
					for (Player member : party.getListOfPlayers()) {
						Profile profileMember = Profile.get(member.getUniqueId());
						if (profileMember.getState() != ProfileState.LOBBY) {
							new MessageFormat(Locale.DUEL_NO_PLAYERS_ON_LOBBY_PARTY.format(playerProfile.getLocale()))
									.send(player);
							return;
						}
					}
					MatchGamePlayer gamePlayer = new MatchGamePlayer(leader.getUniqueId(), leader.getName());
					TeamGameParticipant<MatchGamePlayer> participant = new TeamGameParticipant<>(gamePlayer);

					for (Player partyPlayer : party.getListOfPlayers()) {
						if (!partyPlayer.getPlayer().equals(leader)) {
							participant.getPlayers().add(new MatchGamePlayer(partyPlayer.getUniqueId(),
									partyPlayer.getName()));
						}
					}

					if (participantA == null) participantA = participant;
					else participantB = participant;
				}
			} else {
				MatchGamePlayer playerA = new MatchGamePlayer(player.getUniqueId(), player.getName());
				MatchGamePlayer playerB = new MatchGamePlayer(target.getUniqueId(), target.getName());

				participantA = new GameParticipant<>(playerA);
				participantB = new GameParticipant<>(playerB);
			}

			arena.setBusy(true);
			Match match = new BasicTeamMatch(null, duelRequest.getKit(), arena, false, participantA, participantB);
			if (duelRequest.getRounds() > 1) {
				match = new BasicTeamRoundMatch(null, duelRequest.getKit(), arena, false, participantA, participantB, duelRequest.getRounds());
			} else if (match.getKit().getGameRules().isBridge()) {
				match = new BasicTeamRoundMatch(null, duelRequest.getKit(), arena, false, participantA, participantB, HCFPractice.get().getBridgeRounds());
			}
			TaskUtil.run(match::start);
		} else {
			new MessageFormat(Locale.DUEL_DONT_HAVE_DUEL_REQUEST.format(playerProfile.getLocale()))
					.send(player);
		}
	}
}
