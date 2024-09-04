package me.hcfalerts.practice.nametags;

import com.google.common.collect.Lists;
import com.google.common.primitives.Ints;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.nametags.packets.ScoreboardTeamPacketMod;
import me.hcfalerts.practice.nametags.task.NametagTask;
import me.hcfalerts.practice.utilities.TaskUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NameTag {

    @Getter private static final Map<String, Map<String, NametagInfo>> teamMap = new ConcurrentHashMap<>();
    @Getter private static boolean initiated = false;
    @Getter @Setter private static boolean async = true;

    private static final List<NametagInfo> registeredTeams = Collections.synchronizedList(new ArrayList<>());
    private static int teamCreateIndex = 1;
    private static final List<NametagProvider> providers = new ArrayList<>();

    public static void hook() {
        initiated = true;

//        (new NametagThread()).start();
        TaskUtil.runTimerAsync(new NametagTask(), 20L, 20L);
        registerProvider(new NametagProvider.DefaultNametagProvider());
    }

    public static void registerProvider(NametagProvider newProvider) {
        providers.add(newProvider);
        providers.sort((a, b) -> (Ints.compare(b.getWeight(), a.getWeight())));
    }

    public static void reloadPlayer(Player toRefresh) {
        NametagUpdate update = new NametagUpdate(toRefresh);

        if (async) NametagTask.getPendingUpdates().put(update, true);
        else applyUpdate(update);
    }

    public static void reloadOthersFor(Player refreshFor) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (refreshFor != onlinePlayer) reloadPlayer(onlinePlayer, refreshFor);
        }
    }

    public static void reloadPlayer(Player toRefresh, Player refreshFor) {
        NametagUpdate update = new NametagUpdate(toRefresh, refreshFor);

        if(async) NametagTask.getPendingUpdates().put(update, true);
        else applyUpdate(update);
    }

    public static void applyUpdate(NametagUpdate nametagUpdate) {
        if (nametagUpdate.getToRefresh() != null){
            Player toRefreshPlayer = Bukkit.getPlayerExact(nametagUpdate.getToRefresh());

            if (toRefreshPlayer == null) return;

            if (nametagUpdate.getRefreshFor() == null) {
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    reloadPlayerInternal(toRefreshPlayer, onlinePlayer);
                }
            } else {
                Player refreshForPlayer = Bukkit.getPlayerExact(nametagUpdate.getRefreshFor());

                if (refreshForPlayer != null) reloadPlayerInternal(toRefreshPlayer, refreshForPlayer);
            }
        }
    }

    public static void reloadPlayerInternal(Player toRefresh, Player refreshFor) {
        if(!refreshFor.hasMetadata("sl-LoggedIn")) return;

        NametagInfo provided = null;

        for (NametagProvider nametagProvider : providers) {
            provided =  nametagProvider.fetchNametag(toRefresh, refreshFor);
            if (provided != null) break;
        }

        if (provided == null) return;

        Map<String, NametagInfo> teamInfoMap = new HashMap<>();
        
        if (teamMap.containsKey(refreshFor.getName())) teamInfoMap = teamMap.get(refreshFor.getName());
        
        (new ScoreboardTeamPacketMod(provided.getName(), Lists.newArrayList(toRefresh.getName()), 3)).sendToPlayer(refreshFor);
        teamInfoMap.put(toRefresh.getName(), provided);
        teamMap.put(refreshFor.getName(), teamInfoMap);        
    }

    public static void initiatePlayer(Player player) {
        for (NametagInfo registeredTeam : registeredTeams) {
            registeredTeam.getTeamAddPacket().sendToPlayer(player);
        }
    }

    public static NametagInfo getOrCreate(String prefix, String suffix) {
        for (NametagInfo teamInfo : registeredTeams) {
            if (teamInfo.getPrefix().equals(prefix) && teamInfo.getSuffix().equals(suffix)) {
                return (teamInfo);
            }
        }

        NametagInfo newTeam = new NametagInfo(String.valueOf(teamCreateIndex++), prefix, suffix);
        registeredTeams.add(newTeam);

        ScoreboardTeamPacketMod addPacket = newTeam.getTeamAddPacket();

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            addPacket.sendToPlayer(onlinePlayer);
        }

        return (newTeam);
    }
}