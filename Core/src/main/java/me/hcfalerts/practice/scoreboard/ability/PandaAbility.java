package me.hcfalerts.practice.scoreboard.ability;

import com.google.common.collect.Lists;
import dev.panda.ability.PandaAbilityAPI;
import dev.panda.ability.abilities.Ability;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.file.type.BasicConfigurationFile;
import org.bukkit.entity.Player;

import java.util.List;

public class PandaAbility {

    private static final PandaAbilityAPI api = new PandaAbilityAPI();
    private static final BasicConfigurationFile config = HCFPractice.get().getScoreboardConfig();

    public static List<String> getScoreboardLines(Player player) {
        List<String> lines = Lists.newArrayList();
        Profile profile = Profile.get(player.getUniqueId());
        if (profile.getState() == ProfileState.FIGHTING && profile.getMatch().getKit().getGameRules().isHcfTrap()) {
            for (Ability ability : api.getActiveAbility(player)) {
                lines.add(config.getString("BOARD.MATCH.ABILITIES")
                        .replace("{ability}", ability.getName())
                        .replace("{cooldown}", ability.getCooldown(player)));
            }
            if (api.getGlobalCooldown().hasGlobalCooldown(player)) {
                lines.add(config.getString("BOARD.MATCH.ABILITIES")
                        .replace("{ability}", api.getGlobalCooldown().getGlobalCooldownName())
                        .replace("{cooldown}", api.getGlobalCooldown().getGlobalCooldown(player)));
            }
        }
        return lines;
    }

    public static void removeCooldowns(Player player) {
        for (Ability ability : api.getActiveAbility(player)) {
            ability.setCooldown(player, 0L);
        }
        if (api.getGlobalCooldown().hasGlobalCooldown(player)) {
            api.getGlobalCooldown().setGlobalCooldown(player);
        }
    }
}
