package me.hcfalerts.practice.kit.meta;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.potion.PotionEffect;

import java.util.List;

@Getter @Setter
public class KitGameRules {

	private boolean build, spleef, sumo, parkour, healthRegeneration, ranked, showHealth, hcf, bridge, boxing, hcfTrap, skywars, noFood, noFallDamage, soup;
	private int hitDelay = 20;
	private String kbProfile = "default";
	private List<PotionEffect> effects = Lists.newArrayList();

}
