package me.hcfalerts.practice.profile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.clan.ClanInvite;
import me.hcfalerts.practice.duel.DuelProcedure;
import me.hcfalerts.practice.duel.DuelRequest;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitEditorData;
import me.hcfalerts.practice.kit.KitKnockbackEditData;
import me.hcfalerts.practice.kit.KitLoadout;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.mongo.MatchInfo;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.category.Category;
import me.hcfalerts.practice.profile.conversation.ProfileConversations;
import me.hcfalerts.practice.profile.deatheffects.Data;
import me.hcfalerts.practice.profile.file.IProfile;
import me.hcfalerts.practice.profile.file.impl.FlatFileIProfile;
import me.hcfalerts.practice.profile.file.impl.MongoDBIProfile;
import me.hcfalerts.practice.profile.follow.Follow;
import me.hcfalerts.practice.profile.meta.ProfileKitData;
import me.hcfalerts.practice.profile.meta.ProfileKitEditorData;
import me.hcfalerts.practice.profile.meta.ProfileRematchData;
import me.hcfalerts.practice.profile.meta.option.ProfileOptions;
import me.hcfalerts.practice.profile.weight.Weight;
import me.hcfalerts.practice.queue.QueueProfile;
import me.hcfalerts.practice.tablist.TabType;
import me.hcfalerts.practice.tablist.impl.TabList;
import me.hcfalerts.practice.utilities.Cooldown;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.elo.EloUtil;
import me.hcfalerts.practice.utilities.file.languaje.Lang;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.rank.impl.Default;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

@Getter @Setter
public class Profile {

	@Getter private static Map<UUID, Profile> profiles = new HashMap<>();
	@Getter public static MongoCollection<Document> collection;
	@Getter public static IProfile iProfile;

	private UUID uuid;
	private ProfileState state = ProfileState.LOBBY;
	private final ProfileOptions options = new ProfileOptions();
	private final ProfileKitEditorData kitEditorData = new ProfileKitEditorData();
	private final Map<Kit, ProfileKitData> kitData = Maps.newHashMap();
	private final List<DuelRequest> duelRequests = Lists.newArrayList();
	private KitEditorData kitEditorStatus;
	private KitKnockbackEditData kitKnockbackEditData;
	private DuelProcedure duelProcedure;
	private ProfileRematchData rematchData;
	private Party party;
	private Match match;
	private QueueProfile queueProfile;
	private Cooldown enderpearlCooldown = new Cooldown(0), voteCooldown = new Cooldown(0),
			chatCooldown = new Cooldown(0);
	private ProfileConversations conversations = new ProfileConversations(this);
	private Clan clan;
	private Map<String, ClanInvite> invites = Maps.newHashMap();
	private boolean inTournament, online = false;
	private int fishHit, winStreak;
	private List<MatchInfo> matches = Lists.newArrayList();
	private Lang locale = Lang.getByAbbreviation(HCFPractice.get().getMainConfig().getString("DEFAULT_LANG"));
	private KitLoadout selectedKit;
	private String name, color = "&r";
	private Weight weight;
	private TabType tabType = TabList.DEFAULT_TAB_TYPE;
	private Follow follow;
	private Category category;
	private Player focused;
	private Data deathEffect;

	public Profile(UUID uuid) {
		this.uuid = uuid;

		for (Kit kit : Kit.getKits()) {
			this.kitData.put(kit, new ProfileKitData());
		}
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public boolean isFollowing() {
		return follow != null;
	}

	public DuelRequest getDuelRequest(Player sender) {
		for (DuelRequest duelRequest : duelRequests) {
			if (duelRequest.getSender().equals(sender.getUniqueId())) {
				return duelRequest;
			}
		}
		return null;
	}

	public boolean isDuelRequestExpired(DuelRequest duelRequest) {
		if (duelRequest != null) {
			if (duelRequest.isExpired()) {
				duelRequests.remove(duelRequest);
				return true;
			}
		}

		return false;
	}

	public boolean isBusy() {
		return state != ProfileState.LOBBY;
	}

	public void load() {
		iProfile.load(this);
		updateCategory();
	}

	public void save() {
		iProfile.save(this);
	}

	public void follow(Player target) {
//        setState(ProfileState.FOLLOWING);
		follow = new Follow(uuid, target.getUniqueId(), target);
		Follow.getFollows().put(uuid, follow);

		follow.follow();
	}

	public void updateCategory() {
		category = Category.getByElo(EloUtil.getGlobalElo(this));
	}

	public static void init() {
		if (HCFPractice.get().getRankManager().getRank() instanceof Default) {
			for (int i = 0; i < 3; i++) System.out.println("HCFPractice needs a permission system to function properly");
			Bukkit.getServer().getPluginManager().disablePlugin(HCFPractice.get());
		}

		// Players might have joined before the plugin finished loading
		TaskUtil.runLater(() -> {
			if (iProfile instanceof MongoDBIProfile) {
				collection = HCFPractice.get().getMongoDatabase().getCollection("profiles");
				for (Document document : HCFPractice.get().getMongoDatabase().getCollection("profiles").find()) {
					UUID uuid = UUID.fromString(document.getString("uuid"));
					Profile profile = new Profile(uuid);

					try {
						TaskUtil.runAsync(profile::load);
					} catch (Exception e) {
						if (profile.isOnline()) profile.getPlayer().kickPlayer(CC.RED + "The server is loading...");
						throw new IllegalArgumentException("The profile of uuid " + uuid + " could not be loaded, check the database to see what is wrong");
					}

					profiles.put(uuid, profile);
				}
			}
			else if (iProfile instanceof FlatFileIProfile) {
				for (String players : HCFPractice.get().getPlayersConfig().getConfiguration().getConfigurationSection("players").getKeys(false)) {
					UUID uuid = UUID.fromString(players);
					Profile profile = new Profile(uuid);

					try {
						TaskUtil.runAsync(profile::load);
					} catch (Exception e) {
						if (profile.isOnline()) profile.getPlayer().kickPlayer(CC.RED + "The server is loading...");
						continue;
					}

					profiles.put(uuid, profile);
				}
			}
		}, 40L);


		// Expire duel requests
		TaskUtil.runTimerAsync(() -> {
			List<Profile> list = new ArrayList<>();
			for (Profile profile1 : Profile.getProfiles().values()) {
				if (profile1.getPlayer() != null && profile1.getPlayer().isOnline()) {
					list.add(profile1);
				}
			}
			for (Profile profile : list) {
				Iterator<DuelRequest> iterator = profile.duelRequests.iterator();

				while (iterator.hasNext()) {
					DuelRequest duelRequest = iterator.next();

					if (duelRequest.isExpired()) {
						duelRequest.expire();
						iterator.remove();
					}
				}
			}
		}, 60L, 60L);

		// Save every 5 minutes to prevent data loss
		TaskUtil.runTimerAsync(() -> {
			for (Player players : Bukkit.getOnlinePlayers()) Profile.get(players.getUniqueId()).save();
			if (iProfile instanceof FlatFileIProfile) {
				HCFPractice.get().getPlayersConfig().save();
				HCFPractice.get().getPlayersConfig().reload();
			}
		}, 6000L, 6000L);
	}

	public static Profile get(UUID uuid) {
		Profile profile = profiles.get(uuid);
		if (profile == null) profile = new Profile(uuid);
		return profile;
	}

	public static int getHostSlots(UUID uuid) {
		Player player = Bukkit.getPlayer(uuid);
		BasicConfigurationFile config = HCFPractice.get().getEventsConfig();
		int slots = config.getInteger("DEFAULT_HOST_SLOTS");

		for (String key : config.getConfiguration().getConfigurationSection("HOST_SLOTS").getKeys(false)) {
			if (player.hasPermission(config.getString("HOST_SLOTS." + key + ".PERMISSION"))) {
				if (config.getInteger("HOST_SLOTS." + key + ".SLOTS") > slots) {
					slots = config.getInteger("HOST_SLOTS." + key + ".SLOTS");
				}
			}
		}

		return slots;
	}

	public String getName() {
		if (name == null) {
			name = Bukkit.getOfflinePlayer(uuid).getName();
		}
		return name;
	}

	public int addWinStreak() {
		return ++winStreak;
	}

	public void resetWinStreak() {
		winStreak = 0;
	}
}
