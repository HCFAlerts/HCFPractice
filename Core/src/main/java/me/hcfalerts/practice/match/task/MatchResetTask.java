package me.hcfalerts.practice.match.task;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.arena.ArenaType;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.MatchState;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class MatchResetTask extends BukkitRunnable {

	private final Match match;

	@Override
	public void run() {
		if (match.getArena().getType() == ArenaType.STANDALONE) {
			if (!match.getPlacedBlocks().isEmpty()) {
				EditSession editSession = new EditSession(BukkitUtil.getLocalWorld(match.getArena().getSpawnA().getWorld()), Integer.MAX_VALUE);
				editSession.setFastMode(true);

				for (Location location : match.getPlacedBlocks()) {
					try {
						editSession.setBlock(
								new Vector(location.getBlockX(), location.getBlockY(),
										location.getZ()
								), new BaseBlock(0));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				editSession.flushQueue();

				match.getPlacedBlocks().clear();
			}
			if (!match.getChangedBlocks().isEmpty()) {
				EditSession editSession = new EditSession(BukkitUtil.getLocalWorld(match.getArena().getSpawnA().getWorld()), Integer.MAX_VALUE);
				editSession.setFastMode(true);

				for (BlockState blockState : match.getChangedBlocks()) {
					try {
						editSession.setBlock(
								new Vector(blockState.getLocation().getBlockX(), blockState.getLocation().getBlockY(),
										blockState.getLocation().getZ()
								), new BaseBlock(blockState.getTypeId(), blockState.getRawData()));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				editSession.flushQueue();

				if (match.getKit().getGameRules().isBridge() && match.getState() != MatchState.ENDING_MATCH) return;

				match.getChangedBlocks().clear();

				/*if (match.getKit().getGameRules().isBuild() || match.getKit().getGameRules().isSkywars()
						|| match.getKit().getGameRules().isHcfTrap() || match.getKit().getGameRules().isSpleef()) {
					match.getChangedBlocks().clear();
				}*/
			}
			match.getArena().setBusy(false);
			cancel();
		}
	}

}
