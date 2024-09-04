package me.hcfalerts.practice.utilities;

import com.google.common.collect.Lists;
import lombok.experimental.UtilityClass;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

@UtilityClass
public class PotionUtil {

	public String getName(PotionEffectType potionEffectType) {
		if (potionEffectType.getName().equalsIgnoreCase("fire_resistance"))
			return "Fire Resistance";
		else if (potionEffectType.getName().equalsIgnoreCase("speed"))
			return "Speed";
		else if (potionEffectType.getName().equalsIgnoreCase("weakness"))
			return "Weakness";
		else if (potionEffectType.getName().equalsIgnoreCase("slowness"))
			return "Slowness";
		else if (potionEffectType.getName().equalsIgnoreCase("strength"))
			return "Strength";
		else if (potionEffectType.getName().equalsIgnoreCase("absorption"))
			return "Absorption";
		else if (potionEffectType.getName().equalsIgnoreCase("resistance"))
			return "Resistance";
		else if (potionEffectType.getName().equalsIgnoreCase("invisibility"))
			return "Invisibility";
		else return "Unknown";
	}

	public String convertPotionEffectToString(PotionEffect potionEffect) {
		return potionEffect.getType().getName() + ":" + (potionEffect.getDuration()-1) + ":" + potionEffect.getAmplifier();
	}

	public PotionEffect convertStringToPotionEffect(String key) {
		String[] args = key.split(":");
		return new PotionEffect(PotionEffectType.getByName(args[0]), Integer.parseInt(args[1])-1, Integer.parseInt(args[2]));
	}

	public List<PotionEffect> convertStringToListPotionEffect(List<String> keys) {
		List<PotionEffect> potionEffects = Lists.newArrayList();
		for (String key : keys) {
			String[] args = key.split(":");
			potionEffects.add(new PotionEffect(PotionEffectType.getByName(args[0]), Integer.parseInt(args[1])-1, Integer.parseInt(args[2])));
		}
		return potionEffects;
	}
}
