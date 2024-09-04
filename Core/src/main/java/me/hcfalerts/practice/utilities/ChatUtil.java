package me.hcfalerts.practice.utilities;

import java.util.List;
import java.util.stream.Collectors;
import me.clip.placeholderapi.PlaceholderAPI;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class ChatUtil {
   public static String NORMAL_LINE = "&7&m-----------------------------";

   public static String translate(String text) {
      return ChatColor.translateAlternateColorCodes('&', text);
   }

   public static String[] translate(String[] array) {
      for(int i = 0; i < array.length; ++i) {
         array[i] = translate(array[i]);
      }

      return array;
   }

   public static String placeholder(Player player, String text, boolean colorized) {
      if (colorized) {
         return translate(PlaceholderAPIHook.isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders(player, text) : text);
      } else {
         return PlaceholderAPIHook.isPlaceholderAPI() ? PlaceholderAPI.setPlaceholders(player, text) : text;
      }
   }

   public static List<String> translate(List<String> list) {
      return (List)list.stream().map(ChatUtil::translate).collect(Collectors.toList());
   }

   public static void sendMessage(CommandSender sender, String text) {
      if (sender instanceof Player) {
         Player player = (Player)sender;
         player.sendMessage(translate(text));
      } else {
         sender.sendMessage(translate(text));
      }

   }

   public static void sendMessage(CommandSender sender, String[] array) {
      if (sender instanceof Player) {
         Player player = (Player)sender;
         player.sendMessage(translate(array));
      } else {
         sender.sendMessage(array);
      }

   }

   public static String strip(String text) {
      return ChatColor.stripColor(text);
   }

   public static void broadcast(String text) {
      Bukkit.broadcastMessage(text);
   }

   public static void log(String text) {
      Bukkit.getConsoleSender().sendMessage(translate(text));
   }

   public static String capitalize(String str) {
      return WordUtils.capitalize(str);
   }

   public static String toReadable(Enum<?> enu) {
      return WordUtils.capitalize(enu.name().replace("_", " ").toLowerCase());
   }

   private ChatUtil() {
      throw new UnsupportedOperationException("This is a utilities class and cannot be instantiated");
   }
}
