package me.hcfalerts.practice.match.participant;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

public class MatchGamePlayer extends GamePlayer {

	@Getter private final int elo;
	@Getter @Setter private int eloMod;
	@Getter @Setter private Location checkPoint;
	@Getter @Setter private boolean hidePlayers;
	@Getter private int hits, longestCombo, combo, potionsThrown, potionsMissed, kills, checkPoints;

	public MatchGamePlayer(UUID uuid, String username) {
		this(uuid, username, 0);
	}

	public MatchGamePlayer(UUID uuid, String username, int elo) {
		super(uuid, username);

		this.elo = elo;
	}

	public void incrementPotionsThrown() {
		potionsThrown++;
	}

	public void incrementKills() {
		kills++;
	}

	public void incrementPotionsMissed() {
		potionsMissed++;
	}

	public void incrementCheckpoints() {
		checkPoints++;
	}

	public void handleHit() {
		hits++;
		combo++;

		if (combo > longestCombo) {
			longestCombo = combo;
		}
	}

	public boolean isSameCheckpoint(Location location) {
		if (checkPoint == null) {
			return false;
		}
		return checkPoint.getWorld().equals(location.getWorld()) && checkPoint.getBlockX() == location.getBlockX() &&
				checkPoint.getBlockY() == location.getBlockY() && checkPoint.getBlockZ() == location.getBlockZ();
	}

	public void resetCombo() {
		combo = 0;
	}

}
