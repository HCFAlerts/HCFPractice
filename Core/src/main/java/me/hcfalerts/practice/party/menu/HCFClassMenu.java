package me.hcfalerts.practice.party.menu;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.pagination.PaginatedMenu;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class HCFClassMenu extends PaginatedMenu {

    {
        this.setUpdateAfterClick(true);
        this.setAutoUpdate(true);
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&8HCF Classes";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();
        Profile profile = Profile.get(player.getUniqueId());

        Party party = profile.getParty();
        int i = 9;
        for (Player member : party.getListOfPlayers()) {
            buttons.put(i, new ClassButton(member));
            i++;
        }
        return buttons;
    }

    @AllArgsConstructor
    public static class ClassButton extends Button{

        private final Player member;

        @Override
        public boolean shouldUpdate(Player player, ClickType clickType) {
            return true;
        }

        @Override
        public ItemStack getButtonItem(Player player) {

            Profile profile = Profile.get(member.getUniqueId());

            Party party = profile.getParty();

            if (party.getArchers().contains(member.getUniqueId())){
                return new ItemBuilder(Material.LEATHER_CHESTPLATE)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                        .name("&5" + member.getName())
                        .lore("&7Diamond")
                        .lore("&a&l" + StringEscapeUtils.unescapeHtml4("&#9658;") + " &7Archer")
                        .lore(party.getBards().size() == 1 ? "&c&mBard" : "&7Bard")
                        .lore(party.getRogues().size() == 1 ? "&c&mRogue" : "&7Rogue")
                        .build();
            } else if (party.getBards().contains(member.getUniqueId())){
                return new ItemBuilder(Material.GOLD_CHESTPLATE)
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                        .name("&e" + member.getName())
                        .lore("&7Diamond")
                        .lore(party.getArchers().size() == 1 ? "&c&mArcher" : "&7Archer")
                        .lore("&a&l" + StringEscapeUtils.unescapeHtml4("&#9658;") + " &7Bard")
                        .lore(party.getRogues().size() == 1 ? "&c&mRogue" : "&7Rogue")
                        .build();
            }else if (party.getRogues().contains(member.getUniqueId())){
                return new ItemBuilder(Material.CHAINMAIL_CHESTPLATE)
                        .name("&f" + member.getName())
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                        .lore("&7Diamond")
                        .lore(party.getArchers().size() == 1 ? "&c&mArcher" : "&7Archer")
                        .lore(party.getBards().size() == 1 ? "&c&mBard" : "&7Bard")
                        .lore("&a&l" + StringEscapeUtils.unescapeHtml4("&#9658;") + " &7Rogue")
                        .build();
            }else{
                return new ItemBuilder(Material.DIAMOND_CHESTPLATE)
                        .name("&b" + member.getName())
                        .addItemFlag(ItemFlag.HIDE_ATTRIBUTES)
                        .addItemFlag(ItemFlag.HIDE_ENCHANTS)
                        .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)
                        .lore("&a&l" + StringEscapeUtils.unescapeHtml4("&#9658;") + " &7Diamond")
                        .lore(party.getArchers().size() == 1 ? "&c&mArcher" : "&7Archer")
                        .lore(party.getBards().size() == 1 ? "&c&mBard" : "&7Bard")
                        .lore(party.getRogues().size() == 1 ? "&c&mRogue" : "&7Rogue")
                        .build();
            }
        }

        @Override
        public void clicked(Player player, int slot, ClickType clickType, int hotbarSlot) {
            Profile profile = Profile.get(member.getUniqueId());

            Party party = profile.getParty();

            if(party.getListOfPlayers().size() < HCFPractice.get().getMainConfig().getInteger("MATCH.MINIMUM_PLAYERS_TO_PARTY_TEAMFIGHT")){
                player.closeInventory();
                return;
            }

            if (party.getArchers().contains(member.getUniqueId())){
                party.addBard(member);
            }else if (party.getBards().contains(member.getUniqueId())){
                party.addRogue(member);
            }else if (party.getRogues().contains(member.getUniqueId())){
                party.addDiamond(member);
            }else{
                party.addArcher(member);
            }
        }
    }
}
