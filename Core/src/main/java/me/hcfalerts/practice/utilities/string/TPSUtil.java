package me.hcfalerts.practice.utilities.string;

import com.google.common.base.Strings;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

@UtilityClass
public class TPSUtil {

    public String getTPS() {
        return ((getTicksPerSecond() > 18.0D) ? ChatColor.GREEN : ((getTicksPerSecond() > 16.0D) ? ChatColor.YELLOW : ChatColor.RED)) +
                ((getTicksPerSecond() > 20.0D) ? "*" : "") + Math.min(Math.round(getTicksPerSecond() * 100.0D) / 100.0D, 20.0D);
    }

    private double getTicksPerSecond() {
        return Math.min(Math.round(Bukkit.spigot().getTPS()[0] * 100.0D) / 100.0D, 20.0D);
    }

    public String getCoolestTPS(int bars) {
        return getProgressBar(getTicksPerSecond() * 100 / 20, 100, bars, '┃', ChatColor.GREEN, ChatColor.RED);
    }

    public String getProgressBar(double current, int max, int totalBars, char symbol, ChatColor completedColor,
                                 ChatColor notCompletedColor) {
        float percent = (float) (current / max);
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat("" + completedColor + symbol, progressBars)
                + Strings.repeat("" + notCompletedColor + symbol, totalBars - progressBars);
    }
}
