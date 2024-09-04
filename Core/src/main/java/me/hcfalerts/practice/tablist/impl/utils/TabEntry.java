package me.hcfalerts.practice.tablist.impl.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.tablist.impl.GhostlyTablist;

import java.util.UUID;

@Getter @Setter @AllArgsConstructor
public class TabEntry {

    private String id;
    private UUID uuid;
    private String text;
    private GhostlyTablist tab;
    private SkinTexture texture;
    private TabColumn column;
    private int slot, rawSlot, latency;

}
