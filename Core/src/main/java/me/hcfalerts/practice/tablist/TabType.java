package me.hcfalerts.practice.tablist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TabType {

    MODERN("Modern"),
    VANILLA("Vanilla");

    public final String format;

    public static TabType getByName(String name) {
        for (TabType type : values()) {
            if (type.format.equalsIgnoreCase(name)) {
                return type;
            }
        }
        return null;
    }
}
