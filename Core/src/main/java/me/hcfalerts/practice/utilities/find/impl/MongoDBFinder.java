package me.hcfalerts.practice.utilities.find.impl;

import com.mongodb.client.model.Filters;
import lombok.experimental.UtilityClass;
import me.hcfalerts.practice.profile.Profile;
import org.bson.Document;

import java.util.UUID;

@UtilityClass
public class MongoDBFinder {

    public Profile findByName(String name) {
        Document document = Profile.getCollection().find(Filters.eq("name", name)).first();
        if (document != null) {
            Profile profile = new Profile(UUID.fromString(document.getString("uuid")));
            profile.load();
            return profile;
        }
        return null;
    }

    public Profile findByUUID(UUID uuid) {
        Document document = Profile.getCollection().find(Filters.eq("uuid", uuid.toString())).first();
        if (document != null) {
            Profile profile = new Profile(uuid);
            profile.load();
            return profile;
        }
        return null;
    }
}
