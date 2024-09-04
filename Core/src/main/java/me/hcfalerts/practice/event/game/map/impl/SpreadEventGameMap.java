package me.hcfalerts.practice.event.game.map.impl;

import lombok.Getter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.event.impl.spleef.SpleefGameLogic;
import me.hcfalerts.practice.event.impl.sumo.SumoGameLogic;
import me.hcfalerts.practice.event.impl.tntrun.TNTRunGameLogic;
import me.hcfalerts.practice.event.impl.tnttag.TNTTagGameLogic;
import me.hcfalerts.practice.profile.follow.Follow;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.GamePlayer;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SpreadEventGameMap extends EventGameMap {

	@Getter private final List<Location> spawnLocations = new ArrayList<>();

	public SpreadEventGameMap(String mapName) {
		super(mapName);
	}

	@Override
	public void teleportFighters(EventGame game) {
		int i = 0;

		Location[] locations = spawnLocations.toArray(new Location[0]);

		if (game.getGameLogic() instanceof SumoGameLogic) {
			Player participantA = ((SumoGameLogic) game.getGameLogic()).getParticipantA().getLeader().getPlayer();
			Player participantB = ((SumoGameLogic) game.getGameLogic()).getParticipantB().getLeader().getPlayer();

			if (participantA != null) {
				participantA.teleport(locations[0]);

				if (Follow.getByFollowed(participantA.getUniqueId()) != null) Follow.getByFollowed(participantA.getUniqueId()).detect();
			}
			if (participantB != null) {
				participantB.teleport(locations[1]);

				if (Follow.getByFollowed(participantB.getUniqueId()) != null) Follow.getByFollowed(participantB.getUniqueId()).detect();
			}

			VisibilityLogic.handle(participantA, participantB);
			VisibilityLogic.handle(participantB, participantA);
			return;
		}

		for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
			if (game.getGameLogic() instanceof TNTTagGameLogic || game.getGameLogic() instanceof TNTRunGameLogic
			        || game.getGameLogic() instanceof SpleefGameLogic) {
				if (participant.isEliminated()) continue;
				for (GamePlayer gamePlayer : participant.getPlayers()) {
					Player player = gamePlayer.getPlayer();

					if (player != null) {
						player.teleport(locations[i]);

						if (Follow.getByFollowed(player.getUniqueId()) != null) Follow.getByFollowed(player.getUniqueId()).detect();

						i++;

						if (i == locations.length) i = 0;
					}
				}
			}
		}
	}

	@Override
	public boolean isSetup() {
		return spectatorPoint != null && !spawnLocations.isEmpty();
	}

	@Override
	public void save() {
		super.save();

		FileConfiguration config = HCFPractice.get().getEventsConfig().getConfiguration();
		config.set("EVENT_MAPS." + getMapName() + ".TYPE", "SPREAD");
		config.set("EVENT_MAPS." + getMapName() + ".SPECTATOR_POINT", LocationUtil.serialize(spectatorPoint));
		List<String> list = new ArrayList<>();
		for (Location spawnLocation : spawnLocations) {
			list.add(LocationUtil.serialize(spawnLocation));
		}
		config.set("EVENT_MAPS." + getMapName() + ".SPAWN_LOCATIONS", list);

		try {
			config.save(HCFPractice.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
