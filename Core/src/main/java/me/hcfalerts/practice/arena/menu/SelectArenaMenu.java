package me.hcfalerts.practice.arena.menu;

import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.Menu;
import org.bukkit.entity.Player;

import java.util.Map;

public class SelectArenaMenu extends Menu {

	@Override
	public String getTitle(Player player) {
		return "&8Select Arena";
	}

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		return super.getButtons();
	}

}
