package me.hcfalerts.practice.profile.category;

import me.hcfalerts.practice.profile.category.data.CategoryEditorData;
import me.hcfalerts.practice.utilities.chat.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class CategoryCreateListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        for (CategoryEditorData categoryEditorData : Category.getCategoryEditor()) {
            if (categoryEditorData.getUuid().equals(player.getUniqueId())) {
                event.setCancelled(true);
                Category category = categoryEditorData.getCategory();
                String message = event.getMessage();
                if (categoryEditorData.getData().equals("displayname")) {
                    category.setDisplayName(message);
                    player.sendMessage(CC.translate("&aThe displayname &r" + message + " has been set to the category &r" + category.getName()));
                    categoryEditorData.setData("elo");
                    player.sendMessage(CC.translate("&cNow, set the elo of the category &r" + category.getName()));
                }
                else if (categoryEditorData.getData().equals("elo")) {
                    if (!StringUtils.isNumeric(message)) {
                        player.sendMessage(CC.translate("&cThe elo must be a number"));
                        return;
                    }
                    int elo = Integer.parseInt(event.getMessage());
                    category.setElo(elo);
                    player.sendMessage(CC.translate("&aThe elo &r" + elo + "&a has been set to the category &r" + category.getName()));
                    Category.getCategoryEditor().remove(categoryEditorData);
                    Category.getCategories().add(category);
                    Category.save();
                    player.sendMessage(CC.translate("&aCategory &7" + category.getName() + "&a created."));
                }
                break;
            }
        }
    }
}
