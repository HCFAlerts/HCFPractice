package me.hcfalerts.practice.utilities.find.impl;

import lombok.experimental.UtilityClass;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;

import java.util.UUID;

@UtilityClass
public class FlatFileFinder {

    private final BasicConfigurationFile config = HCFPractice.get().getPlayersConfig();

    public Profile findByName(String name) {
        for (String uuid : config.getConfiguration().getConfigurationSection("players").getKeys(false)) {
            if (config.getConfiguration().getString("players." + uuid + ".name").equalsIgnoreCase(name)) {
                Profile profile = new Profile(UUID.fromString(uuid));
                profile.load();
                return profile;
            }
        }
        return null;
    }

    public Profile findByUUID(UUID uuid) {
        if (config.getConfiguration().contains("players." + uuid.toString())) {
            Profile profile = new Profile(uuid);
            profile.load();
            return profile;
        }
        return null;
    }
}
