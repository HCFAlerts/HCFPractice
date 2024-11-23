package me.hcfalerts.practice;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.google.common.collect.Lists;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.arena.ArenaListener;
import me.hcfalerts.practice.chat.impl.Chat;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.clan.ClanListener;
import me.hcfalerts.practice.essentials.Essentials;
import me.hcfalerts.practice.essentials.EssentialsListener;
import me.hcfalerts.practice.essentials.MainCommand;
import me.hcfalerts.practice.essentials.command.CreateWorldCommand;
import me.hcfalerts.practice.event.Event;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameListener;
import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarListener;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.kit.KitEditorListener;
import me.hcfalerts.practice.knockback.Knockback;
import me.hcfalerts.practice.leaderboard.Leaderboard;
import me.hcfalerts.practice.leaderboard.LeaderboardListener;
import me.hcfalerts.practice.leaderboard.PlaceholderAPI;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.MatchListener;
import me.hcfalerts.practice.nametags.NameTag;
import me.hcfalerts.practice.nametags.PracticeTags;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.party.PartyListener;
import me.hcfalerts.practice.party.classes.ClassTask;
import me.hcfalerts.practice.party.classes.archer.ArcherClass;
import me.hcfalerts.practice.party.classes.bard.BardEnergyTask;
import me.hcfalerts.practice.party.classes.bard.BardListener;
import me.hcfalerts.practice.party.classes.rogue.RogueClass;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileListener;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.profile.category.Category;
import me.hcfalerts.practice.profile.category.CategoryCreateListener;
import me.hcfalerts.practice.profile.deatheffects.DeathEffect;
import me.hcfalerts.practice.profile.file.impl.FlatFileIProfile;
import me.hcfalerts.practice.profile.file.impl.MongoDBIProfile;
import me.hcfalerts.practice.profile.modmode.ModModeListener;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.queue.QueueListener;
import me.hcfalerts.practice.scoreboard.BoardAdapter;
import me.hcfalerts.practice.scoreboard.impl.Assemble;
import me.hcfalerts.practice.tablist.TabAdapter;
import me.hcfalerts.practice.tablist.TabType;
import me.hcfalerts.practice.tablist.impl.TabList;
import me.hcfalerts.practice.tournament.TournamentListener;
import me.hcfalerts.practice.utilities.DiscordWebhook;
import me.hcfalerts.practice.utilities.InventoryUtil;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.CommandManager;
import me.hcfalerts.practice.utilities.file.languaje.LanguageConfigurationFile;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.license.License;
import me.hcfalerts.practice.utilities.menu.MenuListener;
import me.hcfalerts.practice.utilities.playerversion.PlayerVersionHandler;
import me.hcfalerts.practice.utilities.rank.RankManager;
import me.hcfalerts.practice.utilities.string.Animation;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

@Getter @Setter
public class HCFPractice extends JavaPlugin {

    private LanguageConfigurationFile lang;
    private BasicConfigurationFile mainConfig, databaseConfig, arenasConfig, kitsConfig, eventsConfig,
            scoreboardConfig, coloredRanksConfig, tabLobbyConfig, tabEventConfig, tabSingleFFAFightConfig,
            tabSingleTeamFightConfig, tabPartyFFAFightConfig, tabPartyTeamFightConfig, leaderboardConfig,
            langConfig, hotbarConfig, playersConfig, clansConfig, worldsConfig, optionsConfig, queueConfig,
            deathEffectsInvConfig, discordWebhookConfig;
    private Essentials essentials;
    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;
    private RankManager rankManager;
    private License license;
    public boolean placeholderAPI = false, serverLoaded = false, lunarClient = false, webHook = false;
    public int inQueues, inFightsTotal, inFightsUnRanked, inFightsRanked, bridgeRounds, rankedSumoRounds;
    public TabList tabList;
    public Assemble assemble;
    public DiscordWebhook discordWebhook;

    @Override
    public void onEnable() {
        loadConfig();
        if (!getDescription().getAuthors().contains("HCFAlerts") || !getDescription().getName().equals("HCFPractice")) {
            for (int i = 0; i < 30; i++)
                Bukkit.getConsoleSender().sendMessage(CC.translate("&cERROR | The plugin.yml has been modified!"));
            Bukkit.getPluginManager().disablePlugins();
            Bukkit.shutdown();
            return;
        }
        CC.successfullyLicense();
        loadConfigMethods();
        loadSaveMethod();
        loadEssentials();
        loadWebhook();
        initManagers();

        registerListeners();
        registerCommands();

        removeCrafting();
        setUpWorld();

        loadPackets();

        runTasks();

        CC.loadPlugin();
    }

    @Override
    public void onDisable() {
        if (tabList != null) tabList.disable();
        if (assemble != null) assemble.getRunnable().stop();
        for (Profile value : Profile.getProfiles().values()) {
            if (value.isOnline()) value.save();
        }
        if (Profile.getIProfile() instanceof FlatFileIProfile) {
            playersConfig.save();
            playersConfig.reload();
        }
        if (EventGame.getActiveGame() != null) EventGame.getActiveGame().getGameLogic().cancelEvent();
        Match.cleanup();
        for (Clan value : Clan.getClans().values()) {
            value.save();
        }
        for (Kit kit : Kit.getKits()) {
            kit.save();
        }
        kitsConfig.reload();
        for (Arena arena : Arena.getArenas()) {
            arena.save();
        }
        arenasConfig.reload();
        Category.save();
        mainConfig.reload();
        worldsConfig.getConfiguration().set("WORLDS", CreateWorldCommand.VOID_WORLDS);
        worldsConfig.save();
        worldsConfig.reload();
    }

    private void initManagers() {
        this.essentials = new Essentials(this);
        this.rankManager = new RankManager();
        Hotbar.init();
        Kit.init();
        Arena.init();
        Profile.init();
        Match.init();
        Party.init();
        Knockback.init();
        Event.init();
        EventGameMap.init();
        Category.init();
        Clan.init();
        Queue.init();
        Animation.init();
        NameTag.hook();
        BoardAdapter.hook();
        Leaderboard.init();
        PlayerVersionHandler.init();
        Chat.init();
        DeathEffect.init();
        NameTag.registerProvider(new PracticeTags());
        if (mainConfig.getBoolean("TABLIST_ENABLE")) tabList = new TabList(this, new TabAdapter());
        if (mainConfig.getBoolean("MATCH.ENABLE_LUNAR_THINGS")) {
            if (getServer().getPluginManager().getPlugin("LunarClient-API") != null) {
                lunarClient = true;
            } else {
                System.out.println("[HCFPractice] LunarClientAPI is not installed!");
            }
        }
        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            System.out.println("[HCFPractice] Placeholders of PlaceholderAPI successfully registered");
            placeholderAPI = true;
            new PlaceholderAPI().register();
        }
    }

    private void loadConfig() {
        this.mainConfig = new BasicConfigurationFile(this, "config");
        this.lang = new LanguageConfigurationFile(this, "languages/lang");
        this.databaseConfig = new BasicConfigurationFile(this, "database");
        this.coloredRanksConfig = new BasicConfigurationFile(this, "ranks");
        this.scoreboardConfig = new BasicConfigurationFile(this, "scoreboard");
        this.langConfig = new BasicConfigurationFile(this, "global-lang");
        this.hotbarConfig = new BasicConfigurationFile(this, "hotbar");
        this.discordWebhookConfig = new BasicConfigurationFile(this, "discord-webhook");
        this.eventsConfig = new BasicConfigurationFile(this, "menus/events");
        this.leaderboardConfig = new BasicConfigurationFile(this, "menus/leaderboard");
        this.optionsConfig = new BasicConfigurationFile(this, "menus/settings");
        this.queueConfig = new BasicConfigurationFile(this, "menus/queue");
        this.deathEffectsInvConfig = new BasicConfigurationFile(this, "menus/cosmetics");
        this.tabEventConfig = new BasicConfigurationFile(this, "tablist/event");
        this.tabLobbyConfig = new BasicConfigurationFile(this, "tablist/lobby");
        this.tabSingleFFAFightConfig = new BasicConfigurationFile(this, "tablist/SingleFFAFight");
        this.tabSingleTeamFightConfig = new BasicConfigurationFile(this, "tablist/SingleTeamFight");
        this.tabPartyFFAFightConfig = new BasicConfigurationFile(this, "tablist/PartyFFAFight");
        this.tabPartyTeamFightConfig = new BasicConfigurationFile(this, "tablist/PartyTeamFight");
        this.arenasConfig = new BasicConfigurationFile(this, "data/arenas");
        this.kitsConfig = new BasicConfigurationFile(this, "data/kits");
        this.worldsConfig = new BasicConfigurationFile(this, "data/worlds");

        if (mainConfig.getString("SAVE_METHOD").equals("FILE") ||
                mainConfig.getString("SAVE_METHOD").equals("FLATFILE")) {
            this.playersConfig = new BasicConfigurationFile(this, "data/players");
            this.clansConfig = new BasicConfigurationFile(this, "data/clans");
        }
    }

    private void loadConfigMethods() {
        switch (mainConfig.getString("SAVE_METHOD")) {
            case "MONGO": case "MONGODB":
                Profile.iProfile = new MongoDBIProfile();
                break;
            case "FLATFILE": case "FILE":
                Profile.iProfile = new FlatFileIProfile();
                break;
        }

        switch (mainConfig.getString("DEFAULT_TAB_TYPE")) {
            case "MODERN":
                TabList.DEFAULT_TAB_TYPE = TabType.MODERN;
                break;
            case "VANILLA":
                TabList.DEFAULT_TAB_TYPE = TabType.VANILLA;
                break;
        }
    }

    private void loadSaveMethod() {
        if (Profile.getIProfile() instanceof MongoDBIProfile) {
            try {
                if (databaseConfig.getBoolean("MONGO.AUTHENTICATION.ENABLED")) {
                    mongoDatabase = new MongoClient(
                            new ServerAddress(
                                    databaseConfig.getString("MONGO.HOST"),
                                    databaseConfig.getInteger("MONGO.PORT")
                            ),
                            MongoCredential.createCredential(
                                    databaseConfig.getString("MONGO.AUTHENTICATION.USERNAME"),
                                    databaseConfig.getString("MONGO.AUTHENTICATION.DATABASE"),
                                    databaseConfig.getString("MONGO.AUTHENTICATION.PASSWORD").toCharArray()
                            ),
                            MongoClientOptions.builder().build()
                    ).getDatabase(databaseConfig.getString("MONGO.DATABASE"));
                } else {
                    mongoDatabase = new MongoClient(databaseConfig.getString("MONGO.HOST"), databaseConfig.getInteger("MONGO.PORT"))
                            .getDatabase(databaseConfig.getString("MONGO.DATABASE"));
                }
            } catch (Exception e) {
                System.out.println("HCFPractice was disabled as it failed to connect to the MongoDB");
                Bukkit.getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    private void runTasks() {
        TaskUtil.runTimer(() -> {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        for (Player other : Bukkit.getOnlinePlayers()) {
                            TaskUtil.runAsync(() -> NameTag.reloadPlayer(player, other));
                        }
                    }
                },
                20L, 20L);
        TaskUtil.runTimerAsync(new ClassTask(), 5L, 5L);
        TaskUtil.runTimer(new BardEnergyTask(), 15L, 20L);
        TaskUtil.runTimer(() -> {
            for (Profile value : Profile.getProfiles().values()) {
                if (value.isOnline() && value.getRematchData() != null) value.getRematchData().validate();
            }
        }, 20L, 20L);
        TaskUtil.runLater(() -> serverLoaded = true, 60L);
    }

    private void setUpWorld() {
        for (String s : worldsConfig.getConfiguration().getStringList("WORLDS")) {
            if (new File(s).exists()) {
                Bukkit.createWorld(new WorldCreator(s));
                CreateWorldCommand.VOID_WORLDS.add(s);
            }
        }

        for (World world : getServer().getWorlds()) {
            world.setDifficulty(Difficulty.HARD);
            world.setGameRuleValue("doDaylightCycle", "false");
            getEssentials().clearEntities(world);
        }
    }

    private void removeCrafting() {
        for (Material craft : Arrays.asList(
                Material.WORKBENCH,
                Material.STICK,
                Material.WOOD_PLATE,
                Material.WOOD_BUTTON,
                Material.SNOW_BLOCK,
                Material.STONE_BUTTON)) {
            InventoryUtil.removeCrafting(craft);
        }
    }

    private void registerListeners() {
        for (Listener listener : Arrays.asList(
                new KitEditorListener(),
                new PartyListener(),
                new ProfileListener(),
                new PartyListener(),
                new MatchListener(),
                new QueueListener(),
                new ArenaListener(),
                new EventGameListener(),
                new BardListener(),
                new ArcherClass(),
                new RogueClass(),
                new ClanListener(),
                new EssentialsListener(),
                new MenuListener(),
                new LeaderboardListener(),
                new TournamentListener(),
                new HotbarListener(),
                new CategoryCreateListener())) {
            getServer().getPluginManager().registerEvents(listener, this);
        }
        if (getMainConfig().getBoolean("MOD_MODE")) getServer().getPluginManager().registerEvents(new ModModeListener(), this);
    }

    public void registerCommands() {
        new CommandManager(this, Lists.newArrayList());
        MainCommand.init();
    }

    private void loadEssentials() {
        this.bridgeRounds = getMainConfig().getInteger("MATCH.ROUNDS_BRIDGE");
        this.rankedSumoRounds = getMainConfig().getInteger("MATCH.ROUNDS_RANKED_SUMO");
    }

    private void loadWebhook() {
        if (!discordWebhookConfig.getString("WEBHOOK").isEmpty()) {
            this.webHook = true;
            try {
                this.discordWebhook = new DiscordWebhook(discordWebhookConfig.getString("WEBHOOK"));
            } catch (Exception e) {
                System.out.println("HCFPractice was disabled as it failed to connect to the Discord Webhook");
                Bukkit.getServer().getPluginManager().disablePlugin(this);
            }
        }
    }

    private void loadPackets() {
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, PacketType.Play.Client.BLOCK_PLACE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPlayer().getItemInHand().getType() == Material.MUSHROOM_SOUP) {
                    Player player = event.getPlayer();
                    if (Profile.get(player.getUniqueId()).getState() == ProfileState.FIGHTING) {
                        Match match = Profile.get(player.getUniqueId()).getMatch();

                        if (match.getKit().getGameRules().isSoup()) {
                            double healthAdd = 7.0;
                            if (player.getHealth() + healthAdd >= player.getMaxHealth()) {
                                player.setHealth(player.getMaxHealth());
                            } else if (player.getHealth() + healthAdd < player.getMaxHealth()) {
                                player.setHealth(player.getHealth() + healthAdd);
                            }

                            if (HCFPractice.get().getMainConfig().getBoolean("MATCH.REMOVE_SOUP_ON_CONSUME")) {
                                player.setItemInHand(new ItemStack(Material.AIR));
                            } else {
                                player.setItemInHand(new ItemStack(Material.BOWL));
                            }
                        }
                    }
                }
            }
        });
    }

    public static HCFPractice get(){
        return getPlugin(HCFPractice.class);
    }

}
