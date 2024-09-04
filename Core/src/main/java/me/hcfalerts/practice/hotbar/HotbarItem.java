package me.hcfalerts.practice.hotbar;

import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

public enum HotbarItem {

	QUEUES_MENU(null),
	QUEUE_JOIN_RANKED(null),
	QUEUE_JOIN_UNRANKED(null),
	QUEUE_LEAVE(null),
	PARTY_EVENTS(null),
	PARTY_CREATE("party create"),
	PARTY_DISBAND("party disband"),
	PARTY_LEAVE("party leave"),
	PARTY_INFORMATION("party info"),
	OTHER_PARTIES(null),
	KIT_EDITOR(null),
	SPECTATE_STOP("stopspectating"),
	VIEW_INVENTORY(null),
	EVENT_JOIN("event join"),
	EVENT_LEAVE("event leave"),
	MAP_SELECTION(null),
	REMATCH_REQUEST("rematch"),
	REMATCH_ACCEPT("rematch"),
	CLASS_SELECT(null),
	KIT_SELECTION(null),
	EVENT_SELECT("host"),
	LEADERBOARD_MENU(null),
	RANDOM_TELEPORT(null),
	HIDE_ALL_PLAYERS(null),
	RESET(null),
	FOLLOW("follow exit"),
	PARKOUR_HIDE_PLAYERS(null),
	PARKOUR_LAST_CHECKPOINT(null),
	PARKOUR_RESET(null);

	@Getter private final String command;
	@Getter @Setter private Pattern pattern;
	@Getter @Setter private boolean enabled;

	HotbarItem(String command) {
		this.command = command;
	}

}
