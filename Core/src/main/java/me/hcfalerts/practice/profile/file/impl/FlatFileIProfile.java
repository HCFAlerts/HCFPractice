package me.hcfalerts.practice.profile.file.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitLoadout;
import me.hcfalerts.practice.match.mongo.MatchInfo;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.deatheffects.DeathEffect;
import me.hcfalerts.practice.profile.file.IProfile;
import me.hcfalerts.practice.profile.meta.ProfileKitData;
import me.hcfalerts.practice.utilities.InventoryUtil;
import me.hcfalerts.practice.utilities.file.languaje.Lang;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class FlatFileIProfile implements IProfile {

    private final BasicConfigurationFile config = HCFPractice.get().getPlayersConfig();

    @Override
    public void save(Profile profile) {
        UUID uuid = profile.getUuid();
        String path = "players." + uuid;

        if (profile.isOnline()) config.getConfiguration().set(path + ".name", profile.getPlayer().getName());
        else config.getConfiguration().set(path + ".name", profile.getName());

        config.getConfiguration().set(path + ".lang", profile.getLocale().getAbbreviation());
        config.getConfiguration().set(path + ".color", profile.getColor());

        config.getConfiguration().set(path + ".winStreak", profile.getWinStreak());
        if (profile.getDeathEffect() != null)
            config.getConfiguration().set(path + ".deatheffect", profile.getDeathEffect().getClass().getSimpleName());

//        ConfigurationSection optionsSection = uuidSection.createSection("options");
        config.getConfiguration().set(path + ".options.showScoreboard", profile.getOptions().showScoreboard());
        config.getConfiguration().set(path + ".options.allowSpectators", profile.getOptions().allowSpectators());
        config.getConfiguration().set(path + ".options.receiveDuelRequests", profile.getOptions().receiveDuelRequests());
        config.getConfiguration().set(path + ".options.receivingNewConversations", profile.getOptions().receivingNewConversations());

        for (Map.Entry<Kit, ProfileKitData> entry : profile.getKitData().entrySet()) {
            config.getConfiguration().set(path + ".kitStatistics." + entry.getKey().getName() + ".elo", entry.getValue().getElo());
            config.getConfiguration().set(path + ".kitStatistics." + entry.getKey().getName() + ".won", entry.getValue().getWon());
            config.getConfiguration().set(path + ".kitStatistics." + entry.getKey().getName() + ".lost", entry.getValue().getLost());
        }

        if (profile.getClan() != null) config.getConfiguration().set(path + ".clan", profile.getClan().getName());

        if (!profile.getMatches().isEmpty()) {
            for (int i = 0; i < profile.getMatches().size(); i++) {
                MatchInfo match = profile.getMatches().get(i);

                config.getConfiguration().set(path + ".matches." + i + ".winningParticipant", match.getWinningParticipant());
                config.getConfiguration().set(path + ".matches." + i + ".losingParticipant", match.getLosingParticipant());
                config.getConfiguration().set(path + ".matches." + i + ".kit", match.getKit().getName());
                config.getConfiguration().set(path + ".matches." + i + ".newWinnerElo", match.getNewWinnerElo());
                config.getConfiguration().set(path + ".matches." + i + ".newLoserElo", match.getNewLoserElo());
                config.getConfiguration().set(path + ".matches." + i + ".date", match.getDate());
                config.getConfiguration().set(path + ".matches." + i + ".duration", match.getDuration());
            }
        }

        for (Map.Entry<Kit, ProfileKitData> entry : profile.getKitData().entrySet()) {
            JsonArray kitsArray = new JsonArray();

            for (int i = 0; i < 4; i++) {
                KitLoadout loadout = entry.getValue().getLoadout(i);

                if (loadout != null) {
                    JsonObject kitObject = new JsonObject();
                    kitObject.addProperty("index", i);
                    kitObject.addProperty("name", loadout.getCustomName());
                    kitObject.addProperty("armor", InventoryUtil.itemStackArrayToBase64(loadout.getArmor()));
                    kitObject.addProperty("contents", InventoryUtil.itemStackArrayToBase64(loadout.getContents()));
                    kitsArray.add(kitObject);
                }
            }

            config.getConfiguration().set(path + ".loadouts." + entry.getKey().getName(), kitsArray.toString());
        }

//        config.save();
    }

    @Override
    public void load(Profile profile) {
        UUID uuid = profile.getUuid();
        String path = "players." + uuid;

        if (!config.getConfiguration().contains(path)) {
            this.save(profile);
            return;
        }

        if (config.getConfiguration().contains(path + ".lang"))
            profile.setLocale(Lang.getByAbbreviation(config.getString(path + ".lang")));

        if (config.getConfiguration().contains(path + ".name"))
            profile.setName(config.getString(path + ".name"));

        if (config.getConfiguration().contains(path + ".color"))
            profile.setColor(config.getString(path + ".color"));

        if (config.getConfiguration().contains(path + ".winStreak"))
            profile.setWinStreak(config.getInteger(path + ".winStreak"));

        if (config.getConfiguration().contains(path + ".deatheffect"))
            profile.setDeathEffect(DeathEffect.getByNameAndProfile(config.getString(path + ".deatheffect"), profile));

        profile.getOptions().showScoreboard(config.getBoolean(path + ".options.showScoreboard"));
        profile.getOptions().allowSpectators(config.getBoolean(path + ".options.allowSpectators"));
        profile.getOptions().receiveDuelRequests(config.getBoolean(path + ".options.receiveDuelRequests"));
        profile.getOptions().receivingNewConversations(config.getBoolean(path + ".options.receivingNewConversations"));

        if (config.getConfiguration().contains(path + ".clan"))
            profile.setClan(Clan.getByName(config.getString(path + ".clan")));

        if (config.getConfiguration().contains(path + ".kitStatistics")) {
            for (String key : config.getConfiguration().getConfigurationSection(path + ".kitStatistics").getKeys(false)) {
                Kit kit = Kit.getByName(key);

                if (kit != null) {
                    ProfileKitData profileKitData = new ProfileKitData();
                    profileKitData.setElo(config.getInteger(path + ".kitStatistics." + key + ".elo"));
                    profileKitData.setWon(config.getInteger(path + ".kitStatistics." + key + ".won"));
                    profileKitData.setLost(config.getInteger(path + ".kitStatistics." + key + ".lost"));

                    profile.getKitData().put(kit, profileKitData);
                }
            }
        }

        if (config.getConfiguration().contains(path + ".matches")) {
            for (String key : config.getConfiguration().getConfigurationSection(path + ".matches").getKeys(false)) {

                String winningParticipant = config.getString(path + ".matches." + key + ".winningParticipant");
                String losingParticipant = config.getString(path + ".matches." + key + ".losingParticipant");
                Kit kit = Kit.getByName(config.getString(path + ".matches." + key + ".kit"));
                int newWinnerElo = config.getInteger(path + ".matches." + key + ".newWinnerElo");
                int newLoserElo = config.getInteger(path + ".matches." + key + ".newLoserElo");
                String date = config.getString(path + ".matches." + key + ".date");
                String duration = config.getString(path + ".matches." + key + ".duration");
                profile.getMatches().add(new MatchInfo(winningParticipant, losingParticipant, kit, newWinnerElo, newLoserElo, date, duration));
            }
        }

        if (config.getConfiguration().contains(path + ".loadouts")) {
            for (String key : config.getConfiguration().getConfigurationSection(path + ".loadouts").getKeys(false)) {
                Kit kit = Kit.getByName(key);

                if (kit != null) {
                    JsonArray kitsArray = new JsonParser().parse(config.getString(path + ".loadouts." + key)).getAsJsonArray();
                    KitLoadout[] loadouts = new KitLoadout[4];

                    for (JsonElement kitElement : kitsArray) {
                        JsonObject kitObject = kitElement.getAsJsonObject();

                        KitLoadout loadout = new KitLoadout(kitObject.get("name").getAsString());
                        try {
                            loadout.setArmor(InventoryUtil.itemStackArrayFromBase64(kitObject.get("armor").getAsString()));
                            loadout.setContents(InventoryUtil.itemStackArrayFromBase64(kitObject.get("contents").getAsString()));
                        } catch (IOException ignore) {
                            System.out.print("Player Kit Edited failed!");
                            return;
                        }

                        loadouts[kitObject.get("index").getAsInt()] = loadout;
                    }

                    profile.getKitData().get(kit).setLoadouts(loadouts);
                }
            }
        }
    }
}
