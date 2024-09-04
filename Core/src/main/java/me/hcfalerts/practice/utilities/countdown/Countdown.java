package me.hcfalerts.practice.utilities.countdown;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.string.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class Countdown implements Runnable {
    private final Locale locale;

    private final int[] broadcastAt;
    private final Runnable tickHandler;
    private final Runnable broadcastHandler;
    private final Runnable finishHandler;
    private final Predicate<Player> messageFilter;
    private int seconds;
    private boolean first;
    private final List<Player> playerList;
    // Our scheduled task's assigned id, needed for canceling
    private final Integer assignedTaskId;

    Countdown(int seconds, Locale locale, Runnable tickHandler, Runnable broadcastHandler, Runnable finishHandler, Predicate<Player> messageFilter, List<Player> playerList, int... broadcastAt) {
        this.first = true;
        this.seconds = seconds;
        this.locale = locale;
        this.broadcastAt = broadcastAt;
        this.tickHandler = tickHandler;
        this.broadcastHandler = broadcastHandler;
        this.finishHandler = finishHandler;
        this.messageFilter = messageFilter;
        this.playerList = playerList;
        this.assignedTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(HCFPractice.get(), this, 0L, 20L);
    }

    public static CountdownBuilder of(int amount, TimeUnit unit) {
        return new CountdownBuilder((int) unit.toSeconds(amount));
    }

    public void run() {
        if (!this.first) --this.seconds;
        else this.first = false;
//        replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds)
        for (int index : this.broadcastAt) {
            if (this.seconds == index) {
                if (playerList == null) {
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        Profile profile = Profile.get(player.getUniqueId());
                        Object localePlayer = this.locale.format(profile.getLocale());
                        if (this.messageFilter == null || this.messageFilter.test(player)) {
                            if (localePlayer instanceof List) {
                                List<String> messages = (List<String>) localePlayer;
                                for (String message : messages) {
                                    player.sendMessage(CC.translate(message.replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds, profile.getLocale()))));
                                }
                            }
                            else {
                                player.sendMessage(CC.translate(localePlayer.toString().replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds, profile.getLocale()))));
                            }
                        }
                    }
                } else {
                    for (Player player : playerList) {
                        if (player != null && player.isOnline() &&
                                (this.messageFilter == null || this.messageFilter.test(player))) {
                            Profile profile = Profile.get(player.getUniqueId());
                            Object localePlayer = this.locale.format(profile.getLocale());

                            if (localePlayer instanceof List) {
                                List<String> messages = (List<String>) localePlayer;
                                for (String message : messages) {
                                    player.sendMessage(CC.translate(message.replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds, profile.getLocale()))));
                                }
                            } else {
                                player.sendMessage(CC.translate(localePlayer.toString().replace("{time}", TimeUtils.formatIntoDetailedString(this.seconds, profile.getLocale()))));
                            }
                        }
                    }
                }
                if (this.broadcastHandler != null) {
                    this.broadcastHandler.run();
                }
            }
        }
        if (this.seconds == 0) {
            if (this.finishHandler != null) {
                this.finishHandler.run();
            }
            if (assignedTaskId != null) Bukkit.getScheduler().cancelTask(assignedTaskId);
        } else if (this.tickHandler != null) {
            this.tickHandler.run();
        }
    }

    public void stop(){
        Bukkit.getScheduler().cancelTask(assignedTaskId);
    }

    public int getSecondsRemaining() {
        return this.seconds;
    }
}
