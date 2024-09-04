package me.hcfalerts.practice.match.impl;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import lombok.Getter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.match.MatchState;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.party.classes.HCFClass;
import me.hcfalerts.practice.party.classes.bard.BardEnergyTask;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.scoreboard.ability.PandaAbility;
import me.hcfalerts.practice.utilities.BukkitReflection;
import me.hcfalerts.practice.utilities.KitUtils;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

import static me.hcfalerts.practice.utilities.string.BridgeUtils.getStringPoint;

@Getter
public class BasicTeamRoundMatch extends BasicTeamMatch {

    private final int roundsToWin;

    public BasicTeamRoundMatch(Queue queue, Kit kit, Arena arena, boolean ranked, GameParticipant<MatchGamePlayer> participantA,
                               GameParticipant<MatchGamePlayer> participantB, int roundsToWin) {
        super(queue, kit, arena, ranked, participantA, participantB);
        this.roundsToWin = roundsToWin;
    }

    @Override
    public boolean canEndMatch() {
        if (getKit().getGameRules().isBridge()) {
            return this.getParticipantA().getRoundWins() == roundsToWin || this.getParticipantB().getRoundWins() == roundsToWin;
        }
        return (this.getParticipantA().getRoundWins() + this.getParticipantB().getRoundWins()) == roundsToWin;
    }

    @Override
    public boolean canStartRound() {
        if (getKit().getGameRules().isBridge()) {
            return !(getParticipantA().getRoundWins() == roundsToWin || getParticipantB().getRoundWins() == roundsToWin);
        }
        return !((getParticipantA().getRoundWins() + getParticipantB().getRoundWins()) == roundsToWin);
    }

    @Override
    public boolean canEndRound() {
        if(getKit().getGameRules().isBridge()){
            return this.getParticipantA().isEliminated() || this.getParticipantB().isEliminated();
        }
        return this.getParticipantA().isAllDead() || this.getParticipantB().isAllDead();
    }

    @Override
    public void end() {
        super.end();
        if (getKit().getGameRules().isBridge()) {
            for (GameParticipant<MatchGamePlayer> gameParticipant : getParticipants()) {
                for (MatchGamePlayer gamePlayer : gameParticipant.getPlayers()) {
                    gamePlayer.setDead(true);
                    Player bukkitPlayer = gamePlayer.getPlayer();

                    if (bukkitPlayer != null) {
                        if (getWinningParticipant().getConjoinedNames().equals(getParticipantA().getConjoinedNames())) {
                            new MessageFormat(Locale.MATCH_BRIDGE_SCORED_POINTS_RED
                                    .format(Profile.get(bukkitPlayer.getUniqueId()).getLocale()))
                                    .add("{winner}", getWinningParticipant().getConjoinedNames())
                                    .add("{red_points}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, getRoundsToWin()))
                                    .add("{blue_points}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, getRoundsToWin()))
                                    .send(bukkitPlayer);
                        }
                        else if (getWinningParticipant().getConjoinedNames().equals(getParticipantB().getConjoinedNames())) {
                            new MessageFormat(Locale.MATCH_BRIDGE_SCORED_POINTS_BLUE
                                    .format(Profile.get(bukkitPlayer.getUniqueId()).getLocale()))
                                    .add("{winner}", getWinningParticipant().getConjoinedNames())
                                    .add("{blue_points}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, getRoundsToWin()))
                                    .add("{red_points}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.BLUE, getRoundsToWin()))
                                    .send(bukkitPlayer);
                        }
                        if (bukkitPlayer.hasMetadata("lastAttacker")) {
                            bukkitPlayer.removeMetadata("lastAttacker", HCFPractice.get());
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onRoundEnd() {
        // Store winning participant
        setWinningParticipant(getParticipantA().isAllDead() ? getParticipantB() : getParticipantA());
        getWinningParticipant().setRoundWins(getWinningParticipant().getRoundWins() + 1);
        sendMessage(Locale.MATCH_WINNER_ROUND, new MessageFormat().add("{winner}", getWinningParticipant().getConjoinedNames()));

        // Store losing participant
        setLosingParticipant(getParticipantA().isAllDead() ? getParticipantA() : getParticipantB());

        removeEntities();

        if (!canEndMatch()) {
            if (kit.getGameRules().isBuild() || kit.getGameRules().isBridge() || kit.getGameRules().isHcfTrap() || kit.getGameRules().isSkywars()) {
                if (kit.getGameRules().isBridge() && !HCFPractice.get().getMainConfig().getBoolean("MATCH.REMOVE_BLOCKS_ON_ROUND_END_BRIDGE"))
                    return;

                EditSession editSession = new EditSession(BukkitUtil.getLocalWorld(getArena().getSpawnA().getWorld()), Integer.MAX_VALUE);
                editSession.setFastMode(true);
                for (Location location : getPlacedBlocks()) {
                    try {
                        editSession.setBlock(
                                new com.sk89q.worldedit.Vector(location.getBlockX(), location.getBlockY(), location.getZ()
                                ), new BaseBlock(0));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                editSession.flushQueue();
                getPlacedBlocks().clear();
            }

            state = MatchState.ENDING_ROUND;
            logicTask.setNextAction(HCFPractice.get().getMainConfig().getInteger("MATCH.END_ROUND_TIME"));

            if (getKit().getGameRules().isBridge()) {
                for (GameParticipant<MatchGamePlayer> participant : this.getParticipants()) {
                    for (MatchGamePlayer gamePlayer : participant.getPlayers()) {
                        Player player = gamePlayer.getPlayer();
                        player.setVelocity(new Vector());
                        gamePlayer.setDead(false);
                        Location spawn = getParticipantA().containsPlayer(player.getUniqueId()) ?
                                getArena().getSpawnA() : getArena().getSpawnB();

                        if (spawn.getBlock().getType() == Material.AIR) player.teleport(spawn);
                        else player.teleport(spawn.add(0, 2, 0));

                        Profile profile = Profile.get(player.getUniqueId());
                        if (profile.getSelectedKit() == null) {
                            player.getInventory().setContents(getKit().getKitLoadout().getContents());
                        } else {
                            player.getInventory().setContents(profile.getSelectedKit().getContents());
                        }
                        KitUtils.giveBridgeKit(player);
                    }
                }
            }
        }

        super.onRoundEnd();
    }

    @Override
    public List<String> getScoreboardLines(Player player) {
        List<String> lines = new ArrayList<>();
        BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

        if (getParticipant(player) != null) {
            if (state == MatchState.STARTING_ROUND || state == MatchState.PLAYING_ROUND || state == MatchState.ENDING_ROUND || state == MatchState.ENDING_MATCH) {
                if (getParticipantA().getPlayers().size() == 1 && getParticipantB().getPlayers().size() == 1) {
                    GameParticipant<MatchGamePlayer> yours = getParticipant(player);
                    GameParticipant<MatchGamePlayer> opponent = getParticipantA().equals(yours) ?
                            getParticipantB() : getParticipantA();

                    Profile opponentProfile = Profile.get(opponent.getLeader().getUuid());
                    Profile yoursProfile = Profile.get(yours.getLeader().getUuid());

                    if (state == MatchState.ENDING_MATCH) {
                        for (String s : config.getStringList("BOARD.MATCH.1V1.MATCH_END")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{opponent}", opponent.getLeader().getPlayer().getName())
                                    .replace("{opponent-color}", opponentProfile.getColor()));
                        }
                        return lines;
                    }

                    if (kit.getGameRules().isBoxing()) {
                        String actualHits = "0";
                        if ((yours.getLeader().getHits() - opponent.getLeader().getHits()) > 0) {
                            actualHits = "+" + (yours.getLeader().getHits() - opponent.getLeader().getHits());
                        }
                        else if ((yours.getLeader().getHits() - opponent.getLeader().getHits()) < 0) {
                            actualHits = String.valueOf(yours.getLeader().getHits() - opponent.getLeader().getHits());
                        }
                        for (String s : config.getStringList("BOARD.MATCH.1V1.BOXING_MODE")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{opponent-color}", opponentProfile.getColor())
                                    .replace("{opponent}", opponent.getLeader().getPlayer().getName())
                                    .replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
                                    .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{hits}", actualHits)
                                    .replace("{your-hits}", String.valueOf(yours.getLeader().getHits()))
                                    .replace("{opponent-hits}", String.valueOf(opponent.getLeader().getHits()))
                                    .replace("{combo}", String.valueOf(yours.getLeader().getCombo())));
                        }
                        return lines;
                    }
                    else if (kit.getGameRules().isBridge()) {
                        for (String s : config.getStringList("BOARD.MATCH.1V1.BRIDGE.LINES")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{opponent-color}", opponentProfile.getColor())
                                    .replace("{opponent}", opponent.getLeader().getPlayer().getName())
                                    .replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
                                    .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, getRoundsToWin()))
                                    .replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, getRoundsToWin()))
                                    .replace("{kills}", String.valueOf(getParticipant(player).getLeader().getKills())));
                        }
                        return lines;
                    }
                    else if (kit.getGameRules().isHcfTrap()) {
                        for (String s : config.getStringList("BOARD.MATCH.1V1.HCF_TRAP")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{opponent-color}", opponentProfile.getColor())
                                    .replace("{opponent}", opponent.getLeader().getPlayer().getName())
                                    .replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
                                    .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName()));
                        }
                        lines.addAll(PandaAbility.getScoreboardLines(player));
                        return lines;
                    }
                    else if (kit.getGameRules().isHcf() && yoursProfile.getParty() != null) {
                        for (String s : config.getStringList("BOARD.MATCH.1V1.HCF.LINES")) {
                            if (s.contains("{bard-energy}")) {
                                if (yoursProfile.getParty().getBards().contains(player.getUniqueId())) {
                                    for (String s1 : config.getStringList("BOARD.MATCH.1V1.HCF.BARD_ENERGY")) {
                                        lines.add(s1
                                                .replace("{energy}", String.valueOf(BardEnergyTask.getEnergy().get(player.getUniqueId()).intValue())));
                                    }
                                }
                                continue;
                            }
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{opponent-color}", opponentProfile.getColor())
                                    .replace("{opponent}", opponent.getLeader().getPlayer().getName())
                                    .replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
                                    .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{class}", HCFClass.classMap.get(player.getUniqueId()) != null ?
                                            HCFClass.classMap.get(player.getUniqueId()).getName() : "Diamond"));
                        }
                        lines.addAll(PandaAbility.getScoreboardLines(player));
                        return lines;
                    }
                    else if (kit.getGameRules().isParkour()) {
                        for (String s : config.getStringList("BOARD.MATCH.1V1.PARKOUR")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                    .replace("{player}", player.getName())
                                    .replace("{player-checkpoints}", String.valueOf(yours.getLeader().getCheckPoints()))
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{rival}", opponent.getLeader().getPlayer().getName())
                                    .replace("{rival-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
                                    .replace("{rival-checkpoints}", String.valueOf(opponent.getLeader().getCheckPoints())));
                        }
                        return lines;
                    } else {
                        for (String s : config.getStringList("BOARD.MATCH.1V1.DEFAULT")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{opponent-color}", opponentProfile.getColor())
                                    .replace("{opponent}", opponent.getLeader().getPlayer().getName())
                                    .replace("{opponent-ping}", String.valueOf(BukkitReflection.getPing(opponent.getLeader().getPlayer())))
                                    .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName()));
                        }
                        return lines;
                    }
                } else {
                    GameParticipant<MatchGamePlayer> friendly = getParticipant(player);
                    GameParticipant<MatchGamePlayer> opponent = getParticipantA().equals(friendly) ?
                            getParticipantB() : getParticipantA();

                    Profile yourProfile = Profile.get(player.getUniqueId());

                    if (getParticipantA().getPlayers().size() != 1 || getParticipantB().getPlayers().size() != 1) {
                        if (state == MatchState.ENDING_MATCH) {
                            for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.MATCH_END")) {
                                lines.add(s.replace("{duration}", getDuration())
                                        .replace("{arena-author}", getArena().getAuthor())
                                        .replace("{kit}", kit.getName()));
                            }
                            return lines;
                        }

                        if (kit.getGameRules().isBoxing()) {
                            String actualHits = "0";
                            int friendTotalHits = 0;
                            int opponentTotalHits = 0;
                            for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
                                friendTotalHits += friendlyPlayer.getHits();
                            }
                            for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
                                opponentTotalHits += enemyPlayer.getHits();
                            }
                            if ((friendTotalHits - opponentTotalHits) > 0) {
                                actualHits = "+" + (friendTotalHits - opponentTotalHits);
                            }
                            else if ((friendTotalHits - opponentTotalHits) < 0) {
                                actualHits = String.valueOf(friendTotalHits - opponentTotalHits);
                            }
                            for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.BOXING_MODE")) {
                                lines.add(s.replace("{duration}", getDuration())
                                        .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                        .replace("{arena-author}", getArena().getAuthor())
                                        .replace("{kit}", kit.getName())
                                        .replace("{team-hits}", actualHits)
                                        .replace("{hits}", String.valueOf(getGamePlayer(player).getHits()))
                                        .replace("{rival-hits}", String.valueOf(opponentTotalHits))
                                        .replace("{combo}", String.valueOf(getGamePlayer(player).getCombo()))
                                        .replace("{rivals}", String.valueOf(opponent.getPlayers().size())));
                            }
                            return lines;
                        }
                        else if (kit.getGameRules().isBridge()) {
                            for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.BRIDGE.LINES")) {
                                lines.add(s.replace("{duration}", getDuration())
                                        .replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
                                        .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                        .replace("{arena-author}", getArena().getAuthor())
                                        .replace("{kit}", kit.getName())
                                        .replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, getRoundsToWin()))
                                        .replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, getRoundsToWin()))
                                        .replace("{kills}", String.valueOf(getParticipant(player).getLeader().getKills())));
                            }
                            return lines;
                        }
                        else if (kit.getGameRules().isHcfTrap()) {
                            int friendAlive = 0;
                            int rivalsAlive = 0;
                            for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
                                if (!friendlyPlayer.isDead()) {
                                    friendAlive++;
                                }
                            }
                            for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
                                if (!enemyPlayer.isDead()) {
                                    rivalsAlive++;
                                }
                            }
                            for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.HCF_TRAP")) {
                                lines.add(s.replace("{duration}", getDuration())
                                        .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                        .replace("{arena-author}", getArena().getAuthor())
                                        .replace("{kit}", kit.getName())
                                        .replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
                                        .replace("{rivals-alive}", String.valueOf(rivalsAlive))
                                        .replace("{yours}", String.valueOf(friendly.getPlayers().size()))
                                        .replace("{yours-alive}", String.valueOf(friendAlive)));
                            }
                            lines.addAll(PandaAbility.getScoreboardLines(player));
                            return lines;
                        }
                        else if (kit.getGameRules().isHcf() && yourProfile.getParty() != null) {
                            int friendAlive = 0;
                            int rivalsAlive = 0;
                            for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
                                if (!friendlyPlayer.isDead()) {
                                    friendAlive++;
                                }
                            }
                            for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
                                if (!enemyPlayer.isDead()) {
                                    rivalsAlive++;
                                }
                            }
                            for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.HCF.LINES")) {
                                if (s.contains("{bard-energy}")) {
                                    if (yourProfile.getParty().getBards().contains(player.getUniqueId())) {
                                        for (String s1 : config.getStringList("BOARD.MATCH.MASS_FIGHT.HCF.BARD_ENERGY")) {
                                            lines.add(s1
                                                    .replace("{energy}", String.valueOf(BardEnergyTask.getEnergy().get(player.getUniqueId()).intValue())));
                                        }
                                    }
                                    continue;
                                }
                                lines.add(s.replace("{duration}", getDuration())
                                        .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                        .replace("{arena-author}", getArena().getAuthor())
                                        .replace("{kit}", kit.getName())
                                        .replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
                                        .replace("{rivals-alive}", String.valueOf(rivalsAlive))
                                        .replace("{yours}", String.valueOf(friendly.getPlayers().size()))
                                        .replace("{yours-alive}", String.valueOf(friendAlive))
                                        .replace("{class}", HCFClass.classMap.get(player.getUniqueId()) != null ?
                                                HCFClass.classMap.get(player.getUniqueId()).getName() : "Diamond"));
                            }
                            lines.addAll(PandaAbility.getScoreboardLines(player));
                            return lines;
                        }
                        else if (kit.getGameRules().isParkour()) {
                            for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.PARKOUR")) {
                                lines.add(s.replace("{duration}", getDuration())
                                        .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                        .replace("{player}", player.getName())
                                        .replace("{arena-author}", getArena().getAuthor())
                                        .replace("{kit}", kit.getName())
                                        .replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
                                        .replace("{yours}", String.valueOf(friendly.getPlayers().size()))
                                        .replace("{yours-checkpoints}", String.valueOf(getGamePlayer(player).getCheckPoints())));
                            }
                            return lines;
                        } else {
                            int friendAlive = 0;
                            int rivalsAlive = 0;
                            for (MatchGamePlayer friendlyPlayer : friendly.getPlayers()) {
                                if (!friendlyPlayer.isDead()) {
                                    friendAlive++;
                                }
                            }
                            for (MatchGamePlayer enemyPlayer : opponent.getPlayers()) {
                                if (!enemyPlayer.isDead()) {
                                    rivalsAlive++;
                                }
                            }
                            for (String s : config.getStringList("BOARD.MATCH.MASS_FIGHT.DEFAULT")) {
                                lines.add(s.replace("{duration}", getDuration())
                                        .replace("{player-ping}", String.valueOf(BukkitReflection.getPing(player)))
                                        .replace("{arena-author}", getArena().getAuthor())
                                        .replace("{kit}", kit.getName())
                                        .replace("{rivals}", String.valueOf(opponent.getPlayers().size()))
                                        .replace("{rivals-alive}", String.valueOf(rivalsAlive))
                                        .replace("{yours}", String.valueOf(friendly.getPlayers().size()))
                                        .replace("{yours-alive}", String.valueOf(friendAlive)));
                            }
                            return lines;
                        }
                    }
                }
            }
        }

        return lines;
    }

    @Override
    public List<String> getSpectatorScoreboardLines() {
        List<String> lines = Lists.newArrayList();
        BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

        if (state == MatchState.STARTING_ROUND || state == MatchState.PLAYING_ROUND || state == MatchState.ENDING_ROUND || state == MatchState.ENDING_MATCH) {
            if (getParticipantA().getPlayers().size() == 1 && getParticipantB().getPlayers().size() == 1) {
                GameParticipant<MatchGamePlayer> second = getParticipantA();
                GameParticipant<MatchGamePlayer> first = getParticipantB();

                Profile firstProfile = Profile.get(first.getLeader().getUuid());
                Profile secondProfile = Profile.get(second.getLeader().getUuid());

                if (state == MatchState.ENDING_MATCH) {
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.MATCH_END")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", kit.getName())
                                .replace("{opponent}", second.getLeader().getPlayer().getName())
                                .replace("{opponent-color}", secondProfile.getColor()));
                    }
                    return lines;
                }
                else if (state == MatchState.ENDING_ROUND) {
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.WAITING_FOR_NEXT_ROUND")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", getKit().getName())
                                .replace("{current-round}", String.valueOf(first.getRoundWins() + second.getRoundWins() + 1)));
                    }
                    return lines;
                }

                if (kit.getGameRules().isBoxing()) {
                    String yoursHits = "0";
                    String opponentHits = "0";
                    if ((first.getLeader().getHits() - second.getLeader().getHits()) > 0) {
                        yoursHits = "+" + (first.getLeader().getHits() - second.getLeader().getHits());
                        opponentHits = String.valueOf(second.getLeader().getHits() - first.getLeader().getHits());
                    }
                    else if ((first.getLeader().getHits() - second.getLeader().getHits()) < 0) {
                        yoursHits = String.valueOf(first.getLeader().getHits() - second.getLeader().getHits());
                        opponentHits = "+" + (second.getLeader().getHits() - first.getLeader().getHits());
                    }
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.BOXING")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{nd-color}", secondProfile.getColor())
                                .replace("{nd-player}", second.getLeader().getPlayer().getName())
                                .replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
                                .replace("{nd-gap}", opponentHits)
                                .replace("{nd-hits}", String.valueOf(second.getLeader().getHits()))
                                .replace("{nd-combo}", String.valueOf(second.getLeader().getCombo()))
                                .replace("{st-color}", firstProfile.getColor())
                                .replace("{st-player}", first.getLeader().getPlayer().getName())
                                .replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
                                .replace("{st-gap}", yoursHits)
                                .replace("{st-hits}", String.valueOf(first.getLeader().getHits()))
                                .replace("{st-combo}", String.valueOf(first.getLeader().getCombo()))
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", kit.getName()));
                    }
                    return lines;
                }
                else if (kit.getGameRules().isBridge()) {
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.BRIDGE.LINES")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{red-color}", secondProfile.getColor())
                                .replace("{red-player}", second.getLeader().getPlayer().getName())
                                .replace("{red-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
                                .replace("{red-kills}", String.valueOf(getParticipant(second.getLeader().getPlayer()).getLeader().getKills()))
                                .replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, getRoundsToWin()))
                                .replace("{blue-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
                                .replace("{blue-kills}", String.valueOf(getParticipant(first.getLeader().getPlayer()).getLeader().getKills()))
                                .replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, getRoundsToWin()))
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", kit.getName()));
                    }
                    return lines;
                }
                else if (kit.getGameRules().isHcfTrap()) {
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.HCF_TRAP")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{nd-color}", secondProfile.getColor())
                                .replace("{nd-player}", second.getLeader().getPlayer().getName())
                                .replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
                                .replace("{st-color}", firstProfile.getColor())
                                .replace("{st-player}", first.getLeader().getPlayer().getName())
                                .replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", kit.getName()));
                    }
                    return lines;
                }
                else if (kit.getGameRules().isHcf() && firstProfile.getParty() != null) {
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.HCF.LINES")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{nd-color}", secondProfile.getColor())
                                .replace("{nd-player}", second.getLeader().getPlayer().getName())
                                .replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
                                .replace("{st-color}", firstProfile.getColor())
                                .replace("{st-player}", first.getLeader().getPlayer().getName())
                                .replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", kit.getName()));
                    }
                    return lines;
                }
                else if (kit.getGameRules().isParkour()) {
                    // TODO: Variable para tomar el que mas checkpoints lleve
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.PARKOUR")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
                                .replace("{st-player}", first.getLeader().getPlayer().getName())
                                .replace("{st-checkpoints}", String.valueOf(first.getLeader().getCheckPoints()))
                                .replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
                                .replace("{nd-player}", second.getLeader().getPlayer().getName())
                                .replace("{nd-checkpoints}", String.valueOf(second.getLeader().getCheckPoints()))
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", kit.getName()));
                    }
                    return lines;
                } else {
                    for (String s : config.getStringList("BOARD.SPECTATE.1V1.DEFAULT")) {
                        lines.add(s.replace("{duration}", getDuration())
                                .replace("{nd-color}", secondProfile.getColor())
                                .replace("{nd-player}", second.getLeader().getPlayer().getName())
                                .replace("{nd-ping}", String.valueOf(BukkitReflection.getPing(second.getLeader().getPlayer())))
                                .replace("{st-color}", firstProfile.getColor())
                                .replace("{st-player}", first.getLeader().getPlayer().getName())
                                .replace("{st-ping}", String.valueOf(BukkitReflection.getPing(first.getLeader().getPlayer())))
                                .replace("{arena-author}", getArena().getAuthor())
                                .replace("{kit}", kit.getName()));
                    }
                    return lines;
                }
            } else {
                GameParticipant<MatchGamePlayer> first = getParticipantA();
                GameParticipant<MatchGamePlayer> second = getParticipantB();

                if (getParticipantA().getPlayers().size() != 1 || getParticipantB().getPlayers().size() != 1) {
                    if (state == MatchState.ENDING_MATCH) {
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.MATCH_END")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{st-total}", String.valueOf(first.getPlayers().size()))
                                    .replace("{nd-total}", String.valueOf(second.getPlayers().size()))
                                    .replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
                        }
                        return lines;
                    }
                    else if (state == MatchState.ENDING_ROUND) {
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.WAITING_FOR_NEXT_ROUND")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", getKit().getName())
                                    .replace("{current-round}", String.valueOf(first.getRoundWins() + second.getRoundWins() + 1)));
                        }
                        return lines;
                    }

                    if (kit.getGameRules().isBoxing()) {
                        String yoursHits = "0";
                        String opponentHits = "0";
						/*if ((first.getLeader().getHits() - second.getLeader().getHits()) > 0) {
							yoursHits = "+" + (first.getLeader().getHits() - second.getLeader().getHits());
							opponentHits = String.valueOf(second.getLeader().getHits() - first.getLeader().getHits());
						}
						else if ((first.getLeader().getHits() - second.getLeader().getHits()) < 0) {
							yoursHits = String.valueOf(first.getLeader().getHits() - second.getLeader().getHits());
							opponentHits = "+" + (second.getLeader().getHits() - first.getLeader().getHits());
						}*/
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.BOXING")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
                        }
                        return lines;
                    }
                    else if (kit.getGameRules().isBridge()) {
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.BRIDGE.LINES")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{rGoal}", getStringPoint(getParticipantA().getRoundWins(), ChatColor.RED, getRoundsToWin()))
                                    .replace("{bGoal}", getStringPoint(getParticipantB().getRoundWins(), ChatColor.BLUE, getRoundsToWin()))
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
                        }
                        return lines;
                    }
                    else if (kit.getGameRules().isHcfTrap()) {
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.HCF_TRAP")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
                        }
                        return lines;
                    }
                    else if (kit.getGameRules().isHcf() && Profile.get(first.getLeader().getUuid()).getParty() != null) {
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.HCF.LINES")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
                        }
                        return lines;
                    }
                    else if (kit.getGameRules().isParkour()) {
                        // TODO: Variable para tomar el que mas checkpoints lleve
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.PARKOUR")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
                        }
                        return lines;
                    } else {
                        for (String s : config.getStringList("BOARD.SPECTATE.MASS_FIGHT.DEFAULT")) {
                            lines.add(s.replace("{duration}", getDuration())
                                    .replace("{arena-author}", getArena().getAuthor())
                                    .replace("{kit}", kit.getName())
                                    .replace("{fighting}", String.valueOf(first.getAliveCount() + second.getAliveCount())));
                        }
                        return lines;
                    }
                }
            }
        }

        return lines;
    }
}