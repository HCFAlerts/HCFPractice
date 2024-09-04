package me.hcfalerts.practice.event.game.map.impl;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.event.impl.gulag.GulagGameLogic;
import me.hcfalerts.practice.event.impl.sumo.SumoGameLogic;
import me.hcfalerts.practice.profile.follow.Follow;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.GamePlayer;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.LocationUtil;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;

public class TeamEventGameMap extends EventGameMap {

	@Getter @Setter private Location spawnPointA;
	@Getter @Setter private Location spawnPointB;

	public TeamEventGameMap(String mapName) {
		super(mapName);
	}

	@Override
	public void teleportFighters(EventGame game) {
		int locationIndex = 0;
		Location[] locations = new Location[]{ spawnPointA, spawnPointB };

		if (game.getGameLogic() instanceof SumoGameLogic) {
			GameParticipant<? extends GamePlayer>[] participants = new GameParticipant[] {
					((SumoGameLogic) game.getGameLogic()).getParticipantA(),
					((SumoGameLogic) game.getGameLogic()).getParticipantB()
			};

			for (GameParticipant<? extends GamePlayer> participant : participants) {
				int processed = 0;

				for (GamePlayer gamePlayer : participant.getPlayers()) {
					processed++;

					Player player = gamePlayer.getPlayer();

					if (player != null) {
						player.teleport(locations[locationIndex]);
						VisibilityLogic.handle(player);

						if (Follow.getByFollowed(player.getUniqueId()) != null) Follow.getByFollowed(player.getUniqueId()).detect();
					}

					if (processed == participant.getPlayers().size()) locationIndex++;
				}
			}
		} else if (game.getGameLogic() instanceof GulagGameLogic) {
			GameParticipant<GamePlayer>[] participants = new GameParticipant[] {
					((GulagGameLogic) game.getGameLogic()).getParticipantA(),
					((GulagGameLogic) game.getGameLogic()).getParticipantB()
			};

			for (GameParticipant<GamePlayer> participant : participants) {
				int processed = 0;

				for (GamePlayer gamePlayer : participant.getPlayers()) {
					processed++;

					Player player = gamePlayer.getPlayer();
					VisibilityLogic.handle(player);

					if (player != null) player.teleport(locations[locationIndex]);

					if (processed == participant.getPlayers().size()) locationIndex++;
				}
			}
		}
	}

	@Override
	public boolean isSetup() {
		return spectatorPoint != null && spawnPointA != null && spawnPointB != null;
	}

	@Override
	public void save() {
		super.save();

		FileConfiguration config = HCFPractice.get().getEventsConfig().getConfiguration();
		config.set("EVENT_MAPS." + getMapName() + ".TYPE", "TEAM");
		config.set("EVENT_MAPS." + getMapName() + ".SPAWN_POINT_A", LocationUtil.serialize(spawnPointA));
		config.set("EVENT_MAPS." + getMapName() + ".SPAWN_POINT_B", LocationUtil.serialize(spawnPointB));

		try {
			config.save(HCFPractice.get().getEventsConfig().getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
