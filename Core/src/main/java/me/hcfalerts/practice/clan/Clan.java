package me.hcfalerts.practice.clan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.file.impl.FlatFileIProfile;
import me.hcfalerts.practice.profile.file.impl.MongoDBIProfile;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.find.ProfileFinder;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.hcfalerts.practice.utilities.chat.CC.translate;

@Getter
public class Clan {

    @Getter private static final Map<String, Clan> clans = Maps.newHashMap();
    @Getter public static MongoCollection<Document> collection;

    @Setter private String name;
    @Setter private ChatColor color = ChatColor.WHITE;
    private final UUID leader;
    private final List<UUID> members = Lists.newArrayList();
    @Setter private int points = 0, tournamentWins = 0;

    public Clan(String name, UUID leader){
        this.name = name;
        this.leader = leader;
    }

    public String getColoredName(){
        return color + name;
    }

    public static Clan getByName(String name) {
        for (Clan value : clans.values()) {
            if (value.getName().equalsIgnoreCase(name)) {
                return value;
            }
        }
        return null;
    }

    public static Clan getByPlayer(Player player) {
        for (Clan value : clans.values()) {
            if (value.getMembers().contains(player.getUniqueId())) {
                return value;
            }
        }
        return null;
    }

    public List<String> getOffPlayers() {
        List<String> list = Lists.newArrayList();
        for (UUID uuid : members) {
            if (Bukkit.getPlayer(uuid) == null || !Bukkit.getPlayer(uuid).isOnline()) {
                list.add(uuid.toString());
            }
        }
        return list;
    }

    public List<Player> getOnPlayers(){
        List<Player> players = Lists.newArrayList();
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                players.add(player);
            }
        }
        return players;
    }

    public void show(Player player) {
        new MessageFormat(Locale.CLAN_SHOW.format(Profile.get(player.getUniqueId()).getLocale()))
            .add("{name}", name)
            .add("{members}", members())
            .add("{points}", String.valueOf(points))
            .add("{leader}", Bukkit.getPlayer(leader) != null ?
                    Profile.get(leader).getName() :
                    ProfileFinder.findProfileByUUID(leader).getName())
            .add("{tournament_wins}", String.valueOf(tournamentWins))
            .send(player);
    }


    private String members() {
        List<String> names = Lists.newArrayList();
        for (Player onPlayer : getOnPlayers()) {
            names.add(ChatColor.GREEN + onPlayer.getName());
        }
        for (String offPlayer : getOffPlayers()) {
            names.add(ChatColor.GRAY + offPlayer);
        }
        return String.join("&7, ", names);
    }

    public void join(Player player) {
        broadcast(Locale.CLAN_JOIN_BROADCAST, new MessageFormat().add("{player_name}", player.getName()));
        new MessageFormat(Locale.CLAN_JOIN
                .format(Profile.get(player.getUniqueId()).getLocale()))
                .send(player);
        Profile profile = Profile.get(player.getUniqueId());
        profile.setClan(this);
        members.add(player.getUniqueId());
    }

    public void disband(Player player) {
        if(!player.getUniqueId().equals(leader) && !player.hasPermission("practice.clan.disband")){
            new MessageFormat(Locale.CLAN_ERROR_ONLY_OWNER
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }
        Profile leader = Profile.get(this.leader);
        leader.setClan(null);
        TaskUtil.runAsync(leader::save);
        TaskUtil.runAsync(() -> {
            for (UUID member : members) {
                Profile profileMember = Profile.get(member);
                profileMember.setClan(null);
                profileMember.save();
            }
        });
        broadcast(Locale.CLAN_DISBAND, new MessageFormat().add("{player_name}", player.getName()));
        members.clear();

        delete();
        clans.remove(this.name);
    }

    private void delete() {
        if (Profile.getIProfile() instanceof MongoDBIProfile) {
            collection.deleteOne(Filters.eq("name", this.name));
        }
        else if (Profile.getIProfile() instanceof FlatFileIProfile) {
            HCFPractice.get().getClansConfig().getConfiguration().set("clans." + this.name, null);
            HCFPractice.get().getClansConfig().save();
            HCFPractice.get().getClansConfig().reload();
        }
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void chat(Player sender, String message) {
        for (Player onPlayer : getOnPlayers()) {
            onPlayer.sendMessage(translate(HCFPractice.get().getMainConfig().getString("CHAT.CLAN_MESSAGE_FORMAT")
                    .replace("{clan}", name)
                    .replace("{prefix}", HCFPractice.get().getRankManager().getRank().getPrefix(sender.getUniqueId()))
                    .replace("{suffix}", HCFPractice.get().getRankManager().getRank().getSuffix(sender.getUniqueId()))
                    .replace("{player}", sender.getName()))
                    .replace("{message}", message));
        }
    }

    public void broadcast(String msg) {
        for (Player onPlayer : getOnPlayers()) {
            onPlayer.sendMessage(translate(msg));
        }
    }

    public void broadcast(Locale locale, MessageFormat messageFormat) {
        for (Player onPlayer : getOnPlayers()) {
            messageFormat.setMessage(locale.format(Profile.get(onPlayer.getUniqueId()).getLocale()));
            messageFormat.send(onPlayer);
        }
    }

    public List<String> getClanScoreboard(){
        List<String> lines = Lists.newArrayList();
        BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();
        String bars = config.getString("LINES.BARS");

        for (String s : config.getStringList("CLAN.LINES")) {
            lines.add(s.replace("{bars}", bars)
                    .replace("{color}", getColor().toString())
                    .replace("{name}", getName())
                    .replace("{size}", String.valueOf(getOnPlayers().size())));
        }
        return lines;
    }

    public static void init(){
        if (Profile.getIProfile() instanceof MongoDBIProfile) {
            collection = HCFPractice.get().getMongoDatabase().getCollection("clans");
            collection.find().forEach((Block<Document>) document -> {
                Clan clan = new Clan(document.getString("name"), UUID.fromString(document.getString("owner")));
                if (document.containsKey("points")) clan.setPoints(document.getInteger("points"));
                if (document.containsKey("color")) clan.setColor(ChatColor.valueOf(document.getString("color")));
                if (document.containsKey("players")) {
                    if (document.get("players") instanceof String) {
                        JsonArray playersArray = new JsonParser().parse(document.getString("players")).getAsJsonArray();
                        for (JsonElement jsonElement : playersArray) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
                            clan.getMembers().add(uuid);
                        }
                    }
                }
                clans.put(clan.getName(), clan);
            });
        }
        else if (Profile.getIProfile() instanceof FlatFileIProfile) {
            for (String key : HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans").getKeys(false)) {
                ConfigurationSection section = HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans." + key);
                Clan clan = new Clan(key, UUID.fromString(section.getString("owner")));
                if (section.contains("points")) clan.setPoints(section.getInt("points"));
                if (section.contains("color")) clan.setColor(ChatColor.valueOf(section.getString("color")));
                if (section.contains("players")) {
                    if (section.get("players") instanceof String) {
                        JsonArray playersArray = new JsonParser().parse(section.getString("players")).getAsJsonArray();
                        for (JsonElement jsonElement : playersArray) {
                            JsonObject jsonObject = jsonElement.getAsJsonObject();
                            UUID uuid = UUID.fromString(jsonObject.get("uuid").getAsString());
                            clan.getMembers().add(uuid);
                        }
                    }
                }
                clans.put(clan.getName(), clan);
            }
        }

        TaskUtil.runTimerAsync(Clan::saveAll, 200, 200);
//        TaskUtil.runTimerAsync(() -> getClans().values().forEach(Clan::save), 200, 200);
    }

    public static void saveAll() {
        if (Profile.getIProfile() instanceof MongoDBIProfile) {
            for (String s : getClans().keySet()) {
                Clan clan = getByName(s);

                Document document = new Document();
                document.put("name", clan.name);
                document.put("owner", clan.leader.toString());
                document.put("points", clan.points);
                document.put("color", clan.color.name());
                JsonArray playersArray = new JsonArray();
                for (UUID uuid : clan.members) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("uuid", uuid.toString());
                    playersArray.add(jsonObject);
                }
                if (playersArray.size() > 0) document.put("players", playersArray.toString());
                TaskUtil.runAsync(() -> collection.replaceOne(Filters.eq("name", clan.name), document, new ReplaceOptions().upsert(true)));
            }
        }
        else if (Profile.getIProfile() instanceof FlatFileIProfile) {
            for (String s : getClans().keySet()) {
                Clan clan = getByName(s);

                ConfigurationSection section;
                if (HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans").contains(clan.name))
                    section = HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans." + clan.name);
                else
                    section = HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans").createSection(clan.name);

                section.set("owner", clan.leader.toString());
                section.set("points", clan.points);
                section.set("color", clan.color.name());
                JsonArray playersArray = new JsonArray();
                for (UUID uuid : clan.members) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("uuid", uuid.toString());
                    playersArray.add(jsonObject);
                }
                if (playersArray.size() > 0) {
                    section.set("players", playersArray.toString());
                }
                HCFPractice.get().getClansConfig().save();
            }
            TaskUtil.runAsync(() -> HCFPractice.get().getClansConfig().reload());
        }
    }

    public void save(){
        save(true);
    }

    public void save(boolean async){
        if (Profile.getIProfile() instanceof MongoDBIProfile) {
            Document document = new Document();
            document.put("name", this.name);
            document.put("owner", this.leader.toString());
            document.put("points", this.points);
            document.put("color", this.color.name());
            JsonArray playersArray = new JsonArray();
            for (UUID member : members) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("uuid", member.toString());
                playersArray.add(jsonObject);
            }
            if (playersArray.size() > 0) {
                document.put("players", playersArray.toString());
            }
            if (async) {
                TaskUtil.runAsync(() ->
                        collection.replaceOne(Filters.eq("name", this.name), document, new ReplaceOptions().upsert(true)));
            } else {
                collection.replaceOne(Filters.eq("name", this.name), document, new ReplaceOptions().upsert(true));
            }
        }
        else if (Profile.getIProfile() instanceof FlatFileIProfile) {
            ConfigurationSection section;
            if (HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans").contains(this.name))
                section = HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans." + this.name);
            else
                section = HCFPractice.get().getClansConfig().getConfiguration().getConfigurationSection("clans").createSection(this.name);

            section.set("owner", this.leader.toString());
            section.set("points", this.points);
            section.set("color", this.color.name());
            JsonArray playersArray = new JsonArray();
            for (UUID member : members) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("uuid", member.toString());
                playersArray.add(jsonObject);
            }
            if (playersArray.size() > 0) {
                section.set("players", playersArray.toString());
            }

            HCFPractice.get().getClansConfig().save();
            if (async) {
                TaskUtil.runAsync(() -> HCFPractice.get().getClansConfig().reload());
            }
            else {
                HCFPractice.get().getClansConfig().reload();
            }
        }
    }
}
