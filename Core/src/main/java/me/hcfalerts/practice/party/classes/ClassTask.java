package me.hcfalerts.practice.party.classes;

import me.hcfalerts.practice.party.Party;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.TaskUtil;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class ClassTask implements Runnable {

    @Override
    public void run() {
        for (Profile profile : Profile.getProfiles().values()) {
            if (profile.getPlayer() != null && profile.getPlayer().isOnline() && profile.getParty() != null && profile.getState() == ProfileState.FIGHTING) {
                Player player = profile.getPlayer();
                if (profile.getState() != ProfileState.FIGHTING) continue;
                Party party = profile.getParty();
                if (party == null) continue;
                if (party.getArchers().contains(player.getUniqueId())) {
                    if (HCFClass.ARCHER.isApply(player)) {
                        TaskUtil.run(() -> HCFClass.ARCHER.equip(player));
                    } else {
                        TaskUtil.run(() -> {
                            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                                player.removePotionEffect(activePotionEffect.getType());
                            }
                        });
                        HCFClass.classMap.remove(player.getUniqueId());
                    }
                } else if (party.getRogues().contains(player.getUniqueId())) {
                    if (HCFClass.ROGUE.isApply(player)){
                        TaskUtil.run(() -> HCFClass.ROGUE.equip(player));
                    } else {
                        TaskUtil.run(() -> {
                            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                                player.removePotionEffect(activePotionEffect.getType());
                            }
                        });
                        HCFClass.classMap.remove(player.getUniqueId());
                    }
                } else if (party.getBards().contains(player.getUniqueId())) {
                    if (HCFClass.BARD.isApply(player)) {
                        TaskUtil.run(() -> HCFClass.BARD.equip(player));
                    } else {
                        TaskUtil.run(() -> {
                            for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
                                player.removePotionEffect(activePotionEffect.getType());
                            }
                        });
                        HCFClass.classMap.remove(player.getUniqueId());
                    }
                }
            }
        }
    }
}
