package me.hcfalerts.practice.utilities.rank;

import java.util.UUID;

public interface IRank {

    String getRankSystem();
    String getName(UUID uuid);
    String getPrefix(UUID uuid);
    String getSuffix(UUID uuid);
    String getColor(UUID uuid);
    int getWeight(UUID uuid);
}
