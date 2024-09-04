package me.hcfalerts.practice.knockback;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.knockback.impl.Default;
import me.hcfalerts.practice.knockback.impl.FoxSpigot;
import me.hcfalerts.practice.knockback.impl.InsanePaperSpigot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

import static me.hcfalerts.practice.utilities.chat.CC.CHAT_BAR;

public class Knockback {

    @Getter @Setter public static KnockbackProfiler knockbackProfiler;

    public static String translate(String in) {
        return ChatColor.translateAlternateColorCodes('&', in);
    }

    public static List<String> translate(List<String> lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
        }

        return toReturn;
    }

    public static String[] translate(String[] lines) {
        List<String> toReturn = new ArrayList<>();

        for (String line : lines) {
            if (line != null) {
                toReturn.add(ChatColor.translateAlternateColorCodes('&', line));
            }
        }

        return toReturn.toArray(new String[toReturn.size()]);
    }

    public static void init() {
        switch (HCFPractice.get().getServer().getName()) {
            case "FoxSpigot":
                knockbackProfiler = new FoxSpigot();
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                Bukkit.getConsoleSender().sendMessage(translate("             &4&lHCFPRACTICE &8- &7Spigot Hook"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fDetected FoxSpigot, hooked in..."));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fFoxSpigot has been successfully hooked"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fFoxSpigot is not fully recommended for HCFPractice"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fFoxSpigot not contain the best performance for +30 players"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fHCFPractice recommend use CarbonSpigot or HCFSpigot, thanks."));
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                break;
            case "CarbonSpigot":
                knockbackProfiler = new Default();
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                Bukkit.getConsoleSender().sendMessage(translate("             &4&lHCFPRACTICE &8- &7Spigot Hook"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fDetected CarbonSpigot, hooked in..."));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fCarbonSpigot has been successfully hooked"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fCarbonSpigot is fully recommended for HCFPractice"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fCarbonSpigot contain good performance for +100 players"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fThanks for use CarbonSpigot with HCFPractice"));
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                break;
            case "VSpigot":
                knockbackProfiler = new Default();
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                Bukkit.getConsoleSender().sendMessage(translate("             &4&lHCFPRACTICE &8- &7Spigot Hook"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fDetected VSpigot, hooked in..."));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fVSpigot has been successfully hooked"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fVSpigot is not fully recommended for HCFPractice"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fVSpigot and VortexSpigot contain bad Knockback, not Smooth"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fHCFPractice recommend use CarbonSpigot or HCFSpigot, thanks."));
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                break;
            case "VortexSpigot":
                knockbackProfiler = new Default();
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                Bukkit.getConsoleSender().sendMessage(translate("             &4&lHCFPRACTICE &8- &7Spigot Hook"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fDetected VortexSpigot, hooked in..."));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fVortexSpigot has been successfully hooked"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fVortexSpigot is not fully recommended for HCFPractice"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fVortexSpigot and VSpigot contain bad Knockback, not Smooth"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fHCFPractice recommend use CarbonSpigot or HCFSpigot, thanks."));
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                break;
            case "InsanePaper":
                knockbackProfiler = new InsanePaperSpigot();
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                Bukkit.getConsoleSender().sendMessage(translate("             &4&lHCFPRACTICE &8- &7Spigot Hook"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fDetected InsanePaperSpigot, hooked in..."));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fInsanePaperSpigot has been successfully hooked"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fInsanePaperSpigot is not fully recommended for HCFPractice"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fInsanePaperSpigot is based-on default Paper spigot, default KB"));
                Bukkit.getConsoleSender().sendMessage(translate("&a● &fHCFPractice recommend use CarbonSpigot or HCFSpigot, thanks."));
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                break;
            case "1.7.10":
                knockbackProfiler = new Default();
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                Bukkit.getConsoleSender().sendMessage(translate("             &4&lHCFPRACTICE &8- &7Spigot Hook"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &cYou don't have a spigot compatible with HCFPractice Knockback..."));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &cAnd you are using 1.7.10 Spigot, &LNOT RECOMMENDED&7"));
                Bukkit.getConsoleSender().sendMessage(translate(""));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &aCompatible Spigots *&lRECOMMENDED&c*"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &a*&lFoxSpigot&a*, *&lCarbonSpigot&a*, VSpigot, VortexSpigot and *&lHCFSpigot&a*"));
                Bukkit.getConsoleSender().sendMessage(translate(""));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7HCFSpigot is the Best PotPvP Spigot in the Market"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7And the Best Spigot for HCFPractice :')"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7Get HCFSpigot for Free on &ndsc.gg/flameclubdevelopment&7"));
                Bukkit.getConsoleSender().sendMessage(translate(""));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7Using Default &l1.7&7 Knockback System &l[Not Recommended for Smooth KB]&7"));
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                break;
            default:
                knockbackProfiler = new Default();
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                Bukkit.getConsoleSender().sendMessage(translate("             &4&lHCFPRACTICE &8- &7Spigot Hook"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &cYou don't have a compatible spigot for HCFPractice Knockback..."));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &cHCFPractice don't recommend use 1.7 spigots [NitroSpigot, PandaSpigot]"));
                Bukkit.getConsoleSender().sendMessage(translate(""));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &aCompatible Spigots *&lRECOMMENDED&a*"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &a*&lFoxSpigot&a*, *&lCarbonSpigot&a*, VSpigot, VortexSpigot and *&lHCFSpigot&a*"));
                Bukkit.getConsoleSender().sendMessage(translate(""));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7HCFSpigot is the Best Free PotPvP Spigot in the Market"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7And the Best Spigot for HCFPractice :')"));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7Get HCFSpigot for Free on &ndsc.gg/flameclubdevelopment&7"));
                Bukkit.getConsoleSender().sendMessage(translate(""));
                Bukkit.getConsoleSender().sendMessage(translate("&7[&c!&7] &7Using Default Knockback System &l[Not Recommended for Smooth KB]&7"));
                Bukkit.getConsoleSender().sendMessage(CHAT_BAR);
                break;
        }
    }
}
