package me.hcfalerts.practice.event;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameLogic;
import me.hcfalerts.practice.event.impl.gulag.GulagEvent;
import me.hcfalerts.practice.event.impl.spleef.SpleefEvent;
import me.hcfalerts.practice.event.impl.sumo.SumoEvent;
import me.hcfalerts.practice.event.impl.tntrun.TNTRunEvent;
import me.hcfalerts.practice.event.impl.tnttag.TNTTagEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public interface Event {

	List<Event> events = new ArrayList<>();

	static void init() {
		add(new SumoEvent());
		add(new SpleefEvent());
		add(new TNTRunEvent());
		add(new GulagEvent());
		add(new TNTTagEvent());
	}

	static void add(Event event) {
		events.add(event);
		for (Listener listener : event.getListeners()) {
			HCFPractice.get().getServer().getPluginManager().registerEvents(listener, HCFPractice.get());
		}
	}

	static <T extends Event> T getEvent(Class<? extends Event> clazz) {
		for (Event event : events) {
			if (event.getClass() == clazz) {
				return (T) clazz.cast(event);
			}
		}

		return null;
	}

	static Event getByName(String name) {
		for (Event event : events) {
			if (event.getName().equalsIgnoreCase(name)) {
				return event;
			}
		}
		return null;
	}

	String getName();

	String getDisplayName();

	List<String> getDescription();

	Location getLobbyLocation();

	void setLobbyLocation(Location location);

	ItemStack getIcon();

	boolean canHost(Player player);

	List<String> getAllowedMaps();

	List<Listener> getListeners();

	default List<Object> getCommands() {
		return new ArrayList<>();
	}

	EventGameLogic start(EventGame game);

	void save();

}
