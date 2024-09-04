package me.hcfalerts.practice.profile.visibility;

import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameState;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.nametags.NameTag;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class VisibilityLogic {

	public static void handle(Player viewer) {
		for (Player target : Bukkit.getOnlinePlayers()) handle(viewer, target);
	}

	public static void handle(Player viewer, List<Player> targets) {
		for (Player target : targets) handle(viewer, target);
	}

	public static void handle(Player viewer, Player target) {
		if (viewer == null || target == null) return;

		Profile viewerProfile = Profile.get(viewer.getUniqueId());
		Profile targetProfile = Profile.get(target.getUniqueId());

		if (viewerProfile.getState() == ProfileState.LOBBY || viewerProfile.getState() == ProfileState.QUEUEING) {
			if (viewer.equals(target)) {
				TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
				return;
			}

			if (targetProfile.getState() == ProfileState.STAFF_MODE) {
				viewer.hidePlayer(target);
				return;
			}

			if (viewerProfile.isFollowing() && viewerProfile.getFollow().getFollowed().equals(target.getUniqueId())) {
				viewer.showPlayer(target);
				return;
			}

			if (viewerProfile.getParty() != null && viewerProfile.getParty().containsPlayer(target.getUniqueId())) {
				viewer.showPlayer(target);
			} else {
				if(!target.hasPermission("practice.practice.see")) viewer.hidePlayer(target);
				else viewer.showPlayer(target);
			}
			TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
		}
		else if (viewerProfile.getState() == ProfileState.FIGHTING) {
			if (viewer.equals(target)) {
				TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
				return;
			}

			if (targetProfile.getState() == ProfileState.STAFF_MODE) {
				viewer.hidePlayer(target);
				return;
			}

			MatchGamePlayer targetGamePlayer = viewerProfile.getMatch().getGamePlayer(target);

			if (targetGamePlayer != null) {
				if (!targetGamePlayer.isDead()) viewer.showPlayer(target);
				else viewer.hidePlayer(target);
			} else {
				viewer.hidePlayer(target);
			}
			TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
		}
		else if (viewerProfile.getState() == ProfileState.EVENT) {
			if (targetProfile.getState() == ProfileState.STAFF_MODE) {
				viewer.hidePlayer(target);
				return;
			}
			else if (targetProfile.getState() != ProfileState.EVENT) {
				viewer.hidePlayer(target);
				return;
			}

			EventGame game = EventGame.getActiveGame();
			if (game.getGameState() == EventGameState.STARTING_EVENT || game.getGameState() == EventGameState.WAITING_FOR_PLAYERS) {
				viewer.showPlayer(target);
			}
			else if (game.getGameState() == EventGameState.STARTING_ROUND || game.getGameState() == EventGameState.PLAYING_ROUND) {
				if (game.getGameLogic().isPlaying(target)) viewer.showPlayer(target);
				else viewer.hidePlayer(target);
			}
			else viewer.hidePlayer(target);
			TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
		}
		else if (viewerProfile.getState() == ProfileState.SPECTATING) {
			if (targetProfile.getState() == ProfileState.STAFF_MODE) {
				viewer.hidePlayer(target);
				return;
			}

			MatchGamePlayer targetGamePlayer = viewerProfile.getMatch().getGamePlayer(target);

			if (targetGamePlayer != null) {
				if (!targetGamePlayer.isDead() && !targetGamePlayer.isDisconnected()) viewer.showPlayer(target);
				else viewer.hidePlayer(target);
			} else viewer.hidePlayer(target);
			TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
		}
		else if (viewerProfile.getState() == ProfileState.STAFF_MODE) {
			if (targetProfile.getState() == ProfileState.STAFF_MODE) {
				viewer.showPlayer(target);
				return;
			}
			if (viewerProfile.getMatch() == null) {
				if(!target.hasPermission("practice.practice.see")) viewer.hidePlayer(target);
				else viewer.showPlayer(target);
				TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
			} else {
				MatchGamePlayer targetGamePlayer = viewerProfile.getMatch().getGamePlayer(target);
				if (targetGamePlayer != null) {
					if (!targetGamePlayer.isDead() && !targetGamePlayer.isDisconnected()) viewer.showPlayer(target);
					else viewer.hidePlayer(target);
				} else viewer.hidePlayer(target);
			}
			TaskUtil.runAsync(() -> NameTag.reloadPlayer(target, viewer));
		}
	}

}
