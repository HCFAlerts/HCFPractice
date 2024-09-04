package me.hcfalerts.practice.profile.menu;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import me.hcfalerts.practice.match.mongo.MatchInfo;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.menu.Button;
import me.hcfalerts.practice.utilities.menu.pagination.PaginatedMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

@RequiredArgsConstructor
public class ViewMatchMenu extends PaginatedMenu {

    private final Profile profile;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "&8Matches of &f" + profile.getName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        Map<Integer, Button> buttons = Maps.newHashMap();

        for (MatchInfo match : profile.getMatches()) {
            buttons.put(buttons.size(), new MatchButton(match));
        }

        return buttons;
    }

    @RequiredArgsConstructor
    private static class MatchButton extends Button{

        private final MatchInfo matchInfo;

        @Override
        public ItemStack getButtonItem(Player player) {
            return new ItemBuilder(Material.PAPER)
                .name("&a" + matchInfo.getWinningParticipant() + " &fvs&c " + matchInfo.getLosingParticipant())
                .lore("&dDate&7:&f " + matchInfo.getDate())
                .lore("&dDuration&7:&f " + matchInfo.getDuration())
                .lore("&dKit&7:&f " + matchInfo.getKit().getName())
                .lore("&dWinner&7:&f " + matchInfo.getWinningParticipant() + "&7(&a+" + matchInfo.getNewWinnerElo() + "&7)")
                .lore("&dLoser&7:&f " + matchInfo.getLosingParticipant() + "&7(&c-" + matchInfo.getNewLoserElo() + "&7)")
                .build();
        }
    }

}