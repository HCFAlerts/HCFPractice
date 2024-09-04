package me.hcfalerts.practice.utilities;

import me.hcfalerts.practice.HCFPractice;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

   public boolean persist() {
      return true;
   }

   public boolean canRegister() {
      return true;
   }

   @NotNull
   public String getAuthor() {
      return "HCFAlerts";
   }

   @NotNull
   public String getVersion() {
      return HCFPractice.getPlugin(HCFPractice.class).getDescription().getVersion();
   }

   @NotNull
   public String getIdentifier() {
      return "hcfpracticeaddon";
   }

   public String onPlaceholderRequest(Player player, @NotNull String identifier) {
      if (player == null) {
         return "";
      } else {
            return null;
         }
      }
}
