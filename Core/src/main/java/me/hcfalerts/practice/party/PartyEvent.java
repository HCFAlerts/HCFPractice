package me.hcfalerts.practice.party;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PartyEvent {

	FFA("FFA"),
	SPLIT("Split"),
	HCFTEAMFIGHT("HCF-TeamFight");

	private final String name;

}
