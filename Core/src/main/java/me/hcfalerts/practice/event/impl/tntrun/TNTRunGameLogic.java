package me.hcfalerts.practice.event.impl.tntrun;

import com.google.common.collect.ImmutableList;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import lombok.Getter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.event.game.EventGame;
import me.hcfalerts.practice.event.game.EventGameLogic;
import me.hcfalerts.practice.event.game.EventGameLogicTask;
import me.hcfalerts.practice.event.game.EventGameState;
import me.hcfalerts.practice.event.game.map.EventGameMap;
import me.hcfalerts.practice.event.game.map.vote.EventGameMapVoteData;
import me.hcfalerts.practice.hotbar.Hotbar;
import me.hcfalerts.practice.hotbar.HotbarItem;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.GamePlayer;
import me.hcfalerts.practice.profile.visibility.VisibilityLogic;
import me.hcfalerts.practice.utilities.Cooldown;
import me.hcfalerts.practice.utilities.PlayerUtil;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TNTRunGameLogic implements EventGameLogic {

    private static final double DISTANCE_NEAR = 0.3;

    private final EventGame game;
    @Getter private final List<GameParticipant<GamePlayer>> participants;
    @Getter private final EventGameLogicTask logicTask;
    private GameParticipant winningParticipant;

    public TNTRunGameLogic(EventGame game) {
        this.game = game;
        participants = game.getParticipants();
        this.logicTask = new EventGameLogicTask(game);
        this.logicTask.runTaskTimer(HCFPractice.get(), 0, 20L);
    }

    @Override
    public EventGameLogicTask getGameLogicTask() {
        return logicTask;
    }

    @Override
    public void startEvent() {
        ((TNTRunEvent)game.getEvent()).getChangedBlocks().clear();
        for (Player player : Bukkit.getOnlinePlayers()) {
            Profile profile = Profile.get(player.getUniqueId());
            new MessageFormat(me.hcfalerts.practice.Locale.EVENT_START.format(profile.getLocale()))
                    .add("{event_name}", game.getEvent().getName())
                    .add("{event_displayname}", game.getEvent().getDisplayName())
                    .add("{size}", String.valueOf(game.getParticipants().size()))
                    .add("{maximum}", String.valueOf(game.getMaximumPlayers()))
                    .send(player);
        }

        int chosenMapVotes = 0;

        for (Map.Entry<EventGameMap, EventGameMapVoteData> entry : game.getVotesData().entrySet()) {
            if (game.getGameMap() == null) {
                game.setGameMap(entry.getKey());
                chosenMapVotes = entry.getValue().getPlayers().size();
            } else {
                if (entry.getValue().getPlayers().size() >= chosenMapVotes) {
                    game.setGameMap(entry.getKey());
                    chosenMapVotes = entry.getValue().getPlayers().size();
                }
            }
        }

        for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    PlayerUtil.reset(player);
                    player.teleport(game.getGameMap().getSpectatorPoint());
                    Hotbar.giveHotbarItems(player);
                }
            }
        }
    }

    @Override
    public boolean canStartEvent() {
        return game.getRemainingParticipants() >= 2;
    }

    @Override
    public void preEndEvent() {
        for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
            if (!participant.isEliminated()) {
                winningParticipant = participant;
                break;
            }
        }

        if (winningParticipant != null) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                Profile profile = Profile.get(player.getUniqueId());
                new MessageFormat(me.hcfalerts.practice.Locale.EVENT_FINISH.format(profile.getLocale()))
                        .add("{event_name}", game.getEvent().getName())
                        .add("{event_displayname}", game.getEvent().getDisplayName())
                        .add("{winner}", HCFPractice.get().getRankManager().getRank().getPrefix(winningParticipant.getLeader().getUuid()) + winningParticipant.getConjoinedNames())
                        .add("{context}", (winningParticipant.getPlayers().size() == 1 ? "has" : "have"))
                        .send(player);
            }
        }
    }

    @Override
    public void endEvent() {
        EventGame.setActiveGame(null);
        EventGame.setCooldown(new Cooldown(30_000L));

        for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    Profile profile = Profile.get(player.getUniqueId());
                    profile.setState(ProfileState.LOBBY);

                    Hotbar.giveHotbarItems(player);
                    HCFPractice.get().getEssentials().teleportToSpawn(player);
                    VisibilityLogic.handle(player);
                }
            }
        }
        TNTRunEvent TNTRunEvent = (TNTRunEvent) game.getEvent();
        EditSession editSession = new EditSession(BukkitUtil.getLocalWorld(game.getGameMap().getSpectatorPoint().getWorld()), Integer.MAX_VALUE);
        editSession.setFastMode(false);
        editSession.flushQueue();
        for (BlockState block : TNTRunEvent.getChangedBlocks()) {
            try {
                editSession.setBlock(
                        new BlockVector(block.getX(), block.getY(), block.getZ()),
                        new BaseBlock(block.getType().getId(), block.getData().getData()));
            } catch (MaxChangedBlocksException e) {
                e.printStackTrace();
            }
        }
        editSession.flushQueue();
        participants.clear();
        for (Profile value : Profile.getProfiles().values()) {
            if (value.getPlayer() != null && value.isOnline() && value.getState() == ProfileState.LOBBY) {
                Hotbar.giveHotbarItems(value.getPlayer());
            }
        }
    }

    @Override
    public boolean canEndEvent() {
        return game.getRemainingParticipants() <= 1;
    }

    @Override
    public void cancelEvent() {
        game.sendMessage(ChatColor.DARK_RED + "The event has been cancelled by an administrator!");

        EventGame.setActiveGame(null);
        EventGame.setCooldown(new Cooldown(30_000L));

        for (GameParticipant<GamePlayer> participant : game.getParticipants()) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    Profile profile = Profile.get(player.getUniqueId());
                    profile.setState(ProfileState.LOBBY);

                    Hotbar.giveHotbarItems(player);

                    HCFPractice.get().getEssentials().teleportToSpawn(player);
                }
            }
        }
        TNTRunEvent TNTRunEvent = (TNTRunEvent) game.getEvent();
        if (!TNTRunEvent.getChangedBlocks().isEmpty()) {
            EditSession editSession = new EditSession(BukkitUtil.getLocalWorld(game.getGameMap().getSpectatorPoint().getWorld()), Integer.MAX_VALUE);
            editSession.setFastMode(false);
            for (BlockState block : TNTRunEvent.getChangedBlocks()) {
                try {
                    editSession.setBlock(
                            new BlockVector(block.getX(), block.getY(), block.getZ()),
                            new BaseBlock(block.getType().getId(), block.getData().getData()));
                } catch (MaxChangedBlocksException e) {
                    e.printStackTrace();
                }
            }
            editSession.flushQueue();
        }
        participants.clear();
        for (Profile value : Profile.getProfiles().values()) {
            if (value.getPlayer() != null && value.isOnline() && value.getState() == ProfileState.LOBBY) {
                Hotbar.giveHotbarItems(value.getPlayer());
            }
        }
    }

    @Override
    public void preStartRound() {

    }

    @Override
    public void startRound() {
        game.sendSound(Sound.ORB_PICKUP, 1.0F, 15F);

        game.getGameMap().teleportFighters(game);

        for (GameParticipant<GamePlayer> participant : participants) {
            for (GamePlayer gamePlayer : participant.getPlayers()) {
                Player player = gamePlayer.getPlayer();

                if (player != null) {
                    player.getInventory().setArmorContents(new ItemStack[4]);
                    player.getInventory().clear();
                    VisibilityLogic.handle(player);
                }
            }
        }
    }

    @Override
    public boolean canStartRound() {
        return false;
    }

    @Override
    public void endRound() { }

    @Override
    public boolean canEndRound() {
        return false;
    }

    @Override
    public void onVote(Player player, EventGameMap gameMap) {
        if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
            game.getGameState() == EventGameState.STARTING_EVENT) {
            EventGameMapVoteData voteData = game.getVotesData().get(gameMap);

            if (voteData != null) {
                if (voteData.hasVote(player.getUniqueId())) {
                    player.sendMessage(ChatColor.RED + "You have already voted for that map!");
                } else {
                    for (EventGameMapVoteData otherVoteData : game.getVotesData().values()) {
                        if (otherVoteData.hasVote(player.getUniqueId())) {
                            otherVoteData.getPlayers().remove(player.getUniqueId());
                        }
                    }

                    voteData.addVote(player.getUniqueId());

                    game.sendMessage(me.hcfalerts.practice.Locale.EVENT_PLAYER_VOTE, new MessageFormat()
                        .add("{player_name}", HCFPractice.get().getRankManager().getRank().getPrefix(player.getUniqueId()) + player.getName())
                        .add("{map_name}", gameMap.getMapName())
                        .add("{votes}", String.valueOf(voteData.getPlayers().size()))
                    );
                }
            } else {
                player.sendMessage(ChatColor.RED + "A map with that name does not exist.");
            }
        } else {
            player.sendMessage(ChatColor.RED + "The event has already started.");
        }
    }

    @Override
    public void onJoin(Player player) {
        game.getParticipants().add(new GameParticipant<>(new GamePlayer(player.getUniqueId(), player.getName())));

        game.sendMessage(me.hcfalerts.practice.Locale.EVENT_PLAYER_JOIN, new MessageFormat()
            .add("{player_name}", HCFPractice.get().getRankManager().getRank().getPrefix(player.getUniqueId()) + player.getName())
            .add("{size}", String.valueOf(game.getParticipants().size()))
            .add("{maximum}", String.valueOf(game.getMaximumPlayers()))
        );

        Profile profile = Profile.get(player.getUniqueId());
        profile.setState(ProfileState.EVENT);

        Hotbar.giveHotbarItems(player);

        for (Map.Entry<EventGameMap, EventGameMapVoteData> entry : game.getVotesData().entrySet()) {
            ItemStack itemStack = Hotbar.getItems().get(HotbarItem.MAP_SELECTION).getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName(itemMeta.getDisplayName().replace("%MAP%", entry.getKey().getMapName()));
            itemStack.setItemMeta(itemMeta);

            player.getInventory().addItem(itemStack);
        }

        player.updateInventory();
        player.teleport(game.getEvent().getLobbyLocation().clone().add(0, 2, 0));

        VisibilityLogic.handle(player);

        for (GameParticipant<GamePlayer> gameParticipant : game.getParticipants()) {
            for (GamePlayer gamePlayer : gameParticipant.getPlayers()) {
                if (!gamePlayer.isDisconnected()) {
                    Player bukkitPlayer = gamePlayer.getPlayer();

                    if (bukkitPlayer != null) {
                        VisibilityLogic.handle(bukkitPlayer, player);
                    }
                }
            }
        }
    }

    @Override
    public void onLeave(Player player) {
        if (isPlaying(player)) onDeath(player, null);

//        participants.remove(getGameParticipant(player));

        Iterator<GameParticipant<GamePlayer>> iterator = game.getParticipants().iterator();

        while (iterator.hasNext()) {
            GameParticipant<GamePlayer> participant = iterator.next();

            if (participant.containsPlayer(player.getUniqueId())) {
                iterator.remove();

                for (GamePlayer gamePlayer : participant.getPlayers()) {
                    if (!gamePlayer.isDisconnected()) {
                        Player bukkitPlayer = gamePlayer.getPlayer();

                        if (bukkitPlayer != null) {
                            if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
                                game.getGameState() == EventGameState.STARTING_EVENT) {

                                game.sendMessage(Locale.EVENT_PLAYER_LEAVE, new MessageFormat()
                                    .add("{player_name}", HCFPractice.get().getRankManager().getRank().getPrefix(player.getUniqueId()) + player.getName())
                                    .add("{remaining}", String.valueOf(game.getRemainingPlayers()))
                                    .add("{maximum}", String.valueOf(game.getMaximumPlayers()))
                                );
                            }

                            Profile profile = Profile.get(bukkitPlayer.getUniqueId());
                            profile.setState(ProfileState.LOBBY);

                            Hotbar.giveHotbarItems(bukkitPlayer);
                            VisibilityLogic.handle(bukkitPlayer, player);

                            HCFPractice.get().getEssentials().teleportToSpawn(bukkitPlayer);
                        }
                    }
                }
            }
        }

        VisibilityLogic.handle(player);
    }

    ImmutableList<Integer> ids = ImmutableList.of(9, 8, 6);

    @Override
    public void onMove(Player player) {
        if (isPlaying(player)) {
            GamePlayer gamePlayer = game.getGamePlayer(player);

            if (gamePlayer != null) {
                if (player.getLocation().getY() <= 0 || player.getLocation().getBlock().getType() == Material.WATER
                        || player.getLocation().getBlock().getType() == Material.STATIONARY_WATER) {
                    if (!gamePlayer.isDead()) {
                        onDeath(player, null);
                        return;
                    }
                }

                TNTRunEvent TNTRunEvent = (TNTRunEvent) game.getEvent();
                Location location = player.getLocation().clone().subtract(0, 1, 0);

                if (location.getBlock().getType() != Material.AIR) {
                    TNTRunEvent.getChangedBlocks().add(location.getBlock().getState());
                    TaskUtil.runLater(() ->
                            location.getBlock().setType(Material.AIR), 5);
                }

                List<Block> blocks = Arrays.asList(
                        location.getWorld().getBlockAt(
                                (int) Math.floor(location.getX() + DISTANCE_NEAR),
                                (int) location.getY(),
                                (int) Math.floor(location.getZ() + DISTANCE_NEAR)),

                        location.getWorld().getBlockAt(
                                (int) Math.floor(location.getX() - DISTANCE_NEAR),
                                (int) location.getY(),
                                (int) Math.floor(location.getZ() + DISTANCE_NEAR)),

                        location.getWorld().getBlockAt(
                                (int) Math.floor(location.getX() + DISTANCE_NEAR),
                                (int) location.getY(),
                                (int) Math.floor(location.getZ() - DISTANCE_NEAR)),

                        location.getWorld().getBlockAt(
                                (int) Math.floor(location.getX() - DISTANCE_NEAR),
                                (int) location.getY(),
                                (int) Math.floor(location.getZ() - DISTANCE_NEAR))
                );

                for (Block block : blocks) {
                    if (block.getType() != Material.AIR) {
                        TNTRunEvent.getChangedBlocks().add(block.getState());
                        TaskUtil.runLater(() -> block.setType(Material.AIR), 5L);
                    }
                }
            }
        }
    }

    @Override
    public void onDeath(Player player, Player killer) {
        if (EventGame.getActiveGame().getGameState() == EventGameState.STARTING_EVENT) return;
        GamePlayer deadGamePlayer = game.getGamePlayer(player);

        if (deadGamePlayer != null) deadGamePlayer.setDead(true);

        GameParticipant<GamePlayer>  gameParticipant = getGameParticipant(player);
        gameParticipant.setEliminated(true);

        player.teleport(game.getGameMap().getSpectatorPoint());
        PlayerUtil.reset(player);
        Hotbar.giveHotbarItems(player);
        VisibilityLogic.handle(player);
        player.setAllowFlight(true);
        player.setFlying(true);

        for (GameParticipant<GamePlayer> participant : participants) {
            if (!participant.isEliminated()) {
                for (GamePlayer gamePlayer : participant.getPlayers()) {
                    Player participantPlayer = gamePlayer.getPlayer();

                    if (participantPlayer != null) {
                        VisibilityLogic.handle(participantPlayer, player);
                    }
                }
            }
        }

        if (canEndEvent()) {
            preEndEvent();
            game.setGameState(EventGameState.ENDING_EVENT);
            logicTask.setNextAction(3);
        }
    }

    @Override
    public void onInteract(PlayerInteractEvent event, Player player, ItemStack target) {

    }

    @Override
    public void onEntityDamageByPlayer(EntityDamageByEntityEvent event, Player player, Player target) {
        event.setCancelled(true);
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event, Player player) {
        event.setCancelled(true);
    }

    @Override
    public void onInventoryClick(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
    }

    @Override
    public boolean isPlaying(Player player) {
        return (game.getGameState() == EventGameState.STARTING_ROUND || game.getGameState() == EventGameState.PLAYING_ROUND)
                && !getGameParticipant(player).isEliminated();
    }

    @Override
    public List<String> getScoreboardEntries() {
        List<String> lines = new ArrayList<>();
        BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();
        config.getStringList("EVENTS.TNTRUN.LINES").forEach(s -> {
            lines.add(s.replace("{event-name}", game.getEvent().getName())
                    .replace("{event-displayname}", game.getEvent().getDisplayName())
                    .replace("{players}", String.valueOf(game.getRemainingPlayers()))
                    .replace("{max-players}", String.valueOf(game.getMaximumPlayers()))
                    .replace("{bars}", CC.SB_BAR));
        });

        switch (game.getGameState()) {
            case WAITING_FOR_PLAYERS: {
                lines.addAll(config.getStringList("EVENTS.TNTRUN.WAITING-FOR-PLAYERS"));
            }
            break;
            case STARTING_EVENT: {
                for (String s : config.getStringList("EVENTS.TNTRUN.STARTING-EVENT")) {
                    lines.add(s.replace("{time}", String.valueOf(game.getGameLogic().getGameLogicTask().getNextActionTime()))
                            .replace("{bars}", CC.SB_BAR));
                }
            }
            break;
            case STARTING_ROUND:
            case ENDING_ROUND: {
                for (String s : config.getStringList("EVENTS.TNTRUN.ENDING-ROUND")) {
                    lines.add(s.replace("{bars}", CC.SB_BAR));
                }
            }
            break;
            case ENDING_EVENT: {
                if (winningParticipant != null) {
                    for (String s : config.getStringList("EVENTS.TNTRUN.ENDING-EVENT")) {
                        lines.add(s.replace("{bars}", CC.SB_BAR)
                                .replace("{winner}", winningParticipant.getConjoinedNames()));
                    }
                }
            }
            break;
        }

        if (game.getGameState() == EventGameState.WAITING_FOR_PLAYERS ||
                game.getGameState() == EventGameState.STARTING_EVENT) {
            for (String s : config.getStringList("EVENTS.TNTRUN.MAP-VOTES")) {
                if (s.contains("{votes-format}")) {
                    game.getVotesData().forEach((map, voteData) -> {
                        lines.add(config.getString("EVENTS.TNTRUN.VOTES-FORMAT")
                                .replace("{map-name}", map.getMapName())
                                .replace("{size}", String.valueOf(voteData.getPlayers().size())));
                    });
                    continue;
                }
                lines.add(s.replace("{bars}", CC.SB_BAR));
            }
        }

        return lines;
    }

    @Override
    public int getRoundNumber() {
        return 1;
    }

    private GameParticipant<GamePlayer> getGameParticipant(Player player) {
        for (GameParticipant<GamePlayer> participant : participants) {
            if (participant.getLeader().getUuid() == player.getUniqueId()) {
                return participant;
            }
        }
        return null;
    }
}