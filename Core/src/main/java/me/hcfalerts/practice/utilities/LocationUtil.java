package me.hcfalerts.practice.utilities;

import lombok.experimental.UtilityClass;
import me.hcfalerts.practice.arena.impl.StandaloneArena;
import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

@UtilityClass
public class LocationUtil {

	public Location[] getFaces(Location start) {
		Location[] faces = new Location[4];
		faces[0] = new Location(start.getWorld(), start.getX() + 1, start.getY(), start.getZ());
		faces[1] = new Location(start.getWorld(), start.getX() - 1, start.getY(), start.getZ());
		faces[2] = new Location(start.getWorld(), start.getX(), start.getY() + 1, start.getZ());
		faces[3] = new Location(start.getWorld(), start.getX(), start.getY() - 1, start.getZ());
		return faces;
	}

	public String serialize(Location location) {
		if (location == null) {
			return "null";
		}

		return location.getWorld().getName() + ":" + location.getX() + ":" + location.getY() + ":" + location.getZ() +
		       ":" + location.getYaw() + ":" + location.getPitch();
	}

	public Location deserialize(String source) {
		if (source == null) {
			return null;
		}

		String[] split = source.split(":");
		World world = Bukkit.getServer().getWorld(split[0]);

		if (world == null) {
			return null;
		}

		return new Location(world, Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
	}

	public boolean isTeamPortal(Player player) {
		Profile profile = Profile.get(player.getUniqueId());
		BasicTeamMatch match = (BasicTeamMatch) profile.getMatch();

		StandaloneArena arena = (StandaloneArena) match.getArena();

		if (match.getParticipantA().containsPlayer(player.getUniqueId())) {
			return arena.getSpawnRed().contains(player.getLocation());
		} else {
			return arena.getSpawnBlue().contains(player.getLocation());
		}
	}

	/*public Location getVoidZone(Player player, boolean up) {
		Location location = player.getLocation().clone();

		if (up) {
			boolean isDoubleZone = false;
			while (location.getY() < 255) {
				location.setY(location.getY() + 1);
			}
		}
		return null;
	}*/

}
