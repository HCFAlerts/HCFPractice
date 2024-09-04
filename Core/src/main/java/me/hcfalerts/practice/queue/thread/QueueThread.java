package me.hcfalerts.practice.queue.thread;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.impl.BasicTeamMatch;
import me.hcfalerts.practice.match.impl.BasicTeamRoundMatch;
import me.hcfalerts.practice.match.mongo.MatchInfo;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.queue.QueueProfile;
import me.hcfalerts.practice.utilities.BukkitReflection;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class QueueThread extends Thread {

    @Override
    public void run() {
        while (true) {
            for (Queue queue : Queue.getQueues()) {
                for (QueueProfile player : queue.getPlayers()) {
                    player.tickRange();
                }

                if (queue.getPlayers().size() < 2) continue;

                for (QueueProfile firstQueueProfile : queue.getPlayers()) {
                    Player firstPlayer = Bukkit.getPlayer(firstQueueProfile.getPlayerUuid());

                    if (firstPlayer == null) continue;

                    Profile firstProfile = Profile.get(firstPlayer.getUniqueId());

                    // Find arena
                    Arena arena = Arena.getRandomArena(queue.getKit());

                    if (arena == null) {
                        queue.getPlayers().remove(firstQueueProfile);
                        new MessageFormat(Locale.QUEUE_NO_ARENAS_AVAILABLE
                                .format(Profile.get(firstPlayer.getUniqueId()).getLocale()))
                                .add("{kit}", queue.getKit().getName())
                                .send(firstPlayer);
                        break;
                    }

                    for (QueueProfile secondQueueProfile : queue.getPlayers()) {
                        if (firstQueueProfile.equals(secondQueueProfile)) continue;

                        Player secondPlayer = Bukkit.getPlayer(secondQueueProfile.getPlayerUuid());

                        if (secondPlayer == null) continue;

                        Profile secondProfile = Profile.get(secondPlayer.getUniqueId());

                        if (queue.isRanked()) {
                            if (Queue.isPingRangeBoolean()) {
                                if (BukkitReflection.getPing(firstPlayer) > Queue.getPingRange() ||
                                        BukkitReflection.getPing(secondPlayer) > Queue.getPingRange()) {
                                    continue;
                                }
                            }
                        }

                        if (queue.isRanked()) {
                            if (!firstQueueProfile.isInRange(secondQueueProfile.getElo()) ||
                                    !secondQueueProfile.isInRange(firstQueueProfile.getElo())) {
                                continue;
                            }
                        }

                        // Update arena
                        arena.setBusy(true);

                        // Remove players from queue
                        queue.getPlayers().remove(firstQueueProfile);
                        queue.getPlayers().remove(secondQueueProfile);

                        MatchGamePlayer playerA = new MatchGamePlayer(firstPlayer.getUniqueId(), firstPlayer.getName(),
                                firstQueueProfile.getElo());

                        MatchGamePlayer playerB = new MatchGamePlayer(secondPlayer.getUniqueId(), secondPlayer.getName(),
                                secondQueueProfile.getElo());

                        GameParticipant<MatchGamePlayer> participantA = new GameParticipant<>(playerA);
                        GameParticipant<MatchGamePlayer> participantB = new GameParticipant<>(playerB);

                        // Create match
                        Match match;

                        if (queue.getKit().getGameRules().isBridge()) {
                            match = new BasicTeamRoundMatch(queue, queue.getKit(), arena, queue.isRanked(),
                                    participantA, participantB, HCFPractice.get().getBridgeRounds());
                        } else if (queue.isRanked() && queue.getKit().getGameRules().isSumo()) {
                            match = new BasicTeamRoundMatch(queue, queue.getKit(), arena, queue.isRanked(),
                                    participantA, participantB, HCFPractice.get().getRankedSumoRounds());
                        } else {
                            match = new BasicTeamMatch(queue, queue.getKit(), arena, queue.isRanked(),
                                    participantA, participantB);
                        }

                        if (queue.isRanked()) {
                            new MessageFormat(Locale.QUEUE_FOUND_RANKED_MATCH
                                    .format(Profile.get(firstPlayer.getUniqueId()).getLocale()))
                                    .add("{name}", firstPlayer.getName())
                                    .add("{elo}", String.valueOf(firstQueueProfile.getElo()))
                                    .add("{opponent}", secondPlayer.getName())
                                    .add("{opponent-elo}", String.valueOf(secondQueueProfile.getElo()))
                                    .send(firstPlayer);
                            new MessageFormat(Locale.QUEUE_FOUND_RANKED_MATCH
                                    .format(Profile.get(secondPlayer.getUniqueId()).getLocale()))
                                    .add("{name}", secondPlayer.getName())
                                    .add("{elo}", String.valueOf(secondQueueProfile.getElo()))
                                    .add("{opponent}", firstPlayer.getName())
                                    .add("{opponent-elo}", String.valueOf(firstQueueProfile.getElo()))
                                    .send(secondPlayer);
                        } else {
                            new MessageFormat(Locale.QUEUE_FOUND_UNRANKED_MATCH
                                    .format(Profile.get(firstPlayer.getUniqueId()).getLocale()))
                                    .add("{name}", firstPlayer.getName())
                                    .add("{opponent}", secondPlayer.getName())
                                    .send(firstPlayer);
                            new MessageFormat(Locale.QUEUE_FOUND_UNRANKED_MATCH
                                    .format(Profile.get(secondPlayer.getUniqueId()).getLocale()))
                                    .add("{name}", secondPlayer.getName())
                                    .add("{opponent}", firstPlayer.getName())
                                    .send(secondPlayer);
                        }

                        if (queue.isRanked() && HCFPractice.get().getMainConfig().getBoolean("MATCH.PERCENT_TO_WIN_MESSAGE")) {
                            double positive1 = 0;
                            double negative1 = 0;

                            double positive2 = 0;
                            double negative2 = 0;

                            if (firstProfile.getMatches().isEmpty()) {
                                positive1 = 1;
                                negative1 = 1;
                            }
                            if (secondProfile.getMatches().isEmpty()) {
                                positive2 = 1;
                                negative2 = 1;
                            }

                            for (MatchInfo matchInfo : firstProfile.getMatches()) {
                                if (matchInfo.getWinningParticipant().contains(firstPlayer.getName())) {
                                    positive1++;
                                } else {
                                    negative1++;
                                }
                            }

                            for (MatchInfo matchInfo : secondProfile.getMatches()) {
                                if (matchInfo.getWinningParticipant().contains(secondPlayer.getName())) {
                                    positive2++;
                                } else {
                                    negative2++;
                                }
                            }

                            double positivePercent1 = (positive1 * 100) / (positive1 + negative1);
                            double positivePercent2 = (positive2 * 100) / (positive2 + negative2);

                            int percent1 = (int) Math.round((positivePercent1 * 100) / (positivePercent1 + positivePercent2));
                            int percent2 = (int) Math.round((positivePercent2 * 100) / (positivePercent1 + positivePercent2));

                            new MessageFormat(Locale.MATCH_PERCENT_MESSAGE.format(firstProfile.getLocale()))
                                    .add("{your-percent}", String.valueOf(percent1))
                                    .add("{opponent-percent}", String.valueOf(percent2))
                                    .send(firstPlayer); // Send message to first player
                            new MessageFormat(Locale.MATCH_PERCENT_MESSAGE.format(secondProfile.getLocale()))
                                    .add("{your-percent}", String.valueOf(percent2))
                                    .add("{opponent-percent}", String.valueOf(percent1))
                                    .send(secondPlayer); // Send message to second player
                        }

                        TaskUtil.run(match::start);
                    }
                }
            }

            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
