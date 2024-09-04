package me.hcfalerts.practice.utilities.string;

import me.hcfalerts.practice.HCFPractice;
import org.bukkit.ChatColor;

import java.util.Collections;

public class BridgeUtils {

    public static String getStringPoint(int points, ChatColor color, int pointsToWin) {
        String x = HCFPractice.get().getLangConfig().getString("MATCH.BRIDGE_SYMBOL");
        return color +
            String.join("", Collections.nCopies(pointsToWin - (pointsToWin - points), x)) + "&f" +
            String.join("", Collections.nCopies(pointsToWin - points, x));
    }
}