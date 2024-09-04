package me.hcfalerts.practice.utilities;

import lombok.Getter;
import org.bukkit.Bukkit;

public final class PlaceholderAPIHook {
   @Getter
   private static boolean placeholderAPI;

   public static void init() {
      if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
         PlaceholderAPIExpansion papi = new PlaceholderAPIExpansion();
         if (!papi.isRegistered()) {
            papi.register();
         }

         setPlaceholderAPI(true);
      }

   }

   private PlaceholderAPIHook() {
      throw new UnsupportedOperationException("This is a utilities class and cannot be instantiated");
   }

    public static void setPlaceholderAPI(boolean placeholderAPI) {
      PlaceholderAPIHook.placeholderAPI = placeholderAPI;
   }
}
