package me.hcfalerts.practice.utilities.string;

import me.hcfalerts.practice.utilities.file.languaje.Lang;

public class TimeUtils {

    public static String formatIntoDetailedString(int secs, Lang lang) {
        if (secs == 0) {
            return "0 seconds";
        }
        final int remainder = secs % 86400;
        final int days = secs / 86400;
        final int hours = remainder / 3600;
        final int minutes = remainder / 60 - hours * 60;
        final int seconds = remainder % 3600 - minutes * 60;
        final String fDays = (days > 0) ? (" " + days + (lang.equals(Lang.ENGLISH) ? " day" : " día") + ((days > 1) ? "s" : "")) : "";
        final String fHours = (hours > 0) ? (" " + hours + (lang.equals(Lang.ENGLISH) ? " hour" : " hora") + ((hours > 1) ? "s" : "")) : "";
        final String fMinutes = (minutes > 0) ? (" " + minutes + (lang.equals(Lang.ENGLISH) ? " minute" : " minuto") + ((minutes > 1) ? "s" : "")) : "";
        final String fSeconds = (seconds > 0) ? (" " + seconds + (lang.equals(Lang.ENGLISH) ? " second" : " segundo") + ((seconds > 1) ? "s" : "")) : "";
        return (fDays + fHours + fMinutes + fSeconds).trim();
    }

    public static String formatLongIntoDetailedString(long secs, Lang lang) {
        final int unconvertedSeconds = (int)secs;
        return formatIntoDetailedString(unconvertedSeconds, lang);
    }

}
