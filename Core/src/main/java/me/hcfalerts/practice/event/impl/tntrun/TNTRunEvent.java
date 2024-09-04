package me.hcfalerts.practice.event.impl.tntrun;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.event.Event;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameLogic;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.LocationUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TNTRunEvent implements Event {

    @Setter private Location lobbyLocation;
    @Getter private final List<String> allowedMaps;
    @Getter private final List<BlockState> changedBlocks;

    public TNTRunEvent() {
        BasicConfigurationFile config = HCFPractice.get().getEventsConfig();

        lobbyLocation = LocationUtil.deserialize(config.getString("EVENTS.TNTRUN.LOBBY_LOCATION"));

        allowedMaps = new ArrayList<>();
        allowedMaps.addAll(config.getStringList("EVENTS.TNTRUN.ALLOWED_MAPS"));
        this.changedBlocks = Lists.newArrayList();
    }

    @Override
    public String getName() {
        return "TNTRun";
    }

    @Override
    public String getDisplayName() {
        return CC.translate(HCFPractice.get().getEventsConfig().getString("EVENTS.TNTRUN.DISPLAYNAME"));
    }

    @Override
    public List<String> getDescription() {
        return HCFPractice.get().getEventsConfig().getStringList("EVENTS.TNTRUN.DESCRIPTION");
    }

    @Override
    public Location getLobbyLocation() {
        return lobbyLocation;
    }

    @Override
    public ItemStack getIcon() {
        return new ItemBuilder(Material.valueOf(HCFPractice.get().getEventsConfig().getString("EVENTS.TNTRUN.ICON"))).build();
    }

    @Override
    public boolean canHost(Player player) {
        return player.hasPermission("practice.event.host.tntrun");
    }

    @Override
    public List<Listener> getListeners() {
        return Collections.emptyList();
    }

    @Override
    public List<Object> getCommands() {
        return Collections.emptyList();
    }

    @Override
    public EventGameLogic start(EventGame game) {
        return new TNTRunGameLogic(game);
    }

    @Override
    public void save() {
        FileConfiguration config = HCFPractice.get().getEventsConfig().getConfiguration();
        config.set("EVENTS.TNTRUN.LOBBY_LOCATION", LocationUtil.serialize(lobbyLocation));
        config.set("EVENTS.TNTRUN.ALLOWED_MAPS", allowedMaps);

        try {
            config.save(HCFPractice.get().getEventsConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}