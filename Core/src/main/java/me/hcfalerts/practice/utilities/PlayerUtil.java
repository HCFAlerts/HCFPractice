package me.hcfalerts.practice.utilities;

import lombok.experimental.UtilityClass;
import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.playerversion.PlayerVersion;
import me.hcfalerts.practice.utilities.playerversion.PlayerVersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

@UtilityClass
public class PlayerUtil {

	public void setLastAttacker(Player victim, Player attacker) {
		victim.setMetadata("lastAttacker", new FixedMetadataValue(HCFPractice.get(), attacker.getUniqueId()));
	}

	public Player getLastAttacker(Player victim) {
		if (victim.hasMetadata("lastAttacker")) {
			return Bukkit.getPlayer((UUID) victim.getMetadata("lastAttacker").get(0).value());
		} else {
			return null;
		}
	}

	public void reset(Player player) {
		reset(player, true);
	}

	public void reset(Player player, boolean resetHeldSlot) {
		player.setHealth(20.0D);
		player.setSaturation(20.0F);
		player.setFallDistance(0.0F);
		player.setFoodLevel(20);
		player.setFireTicks(0);
		player.setMaximumNoDamageTicks(20);
		player.setExp(0.0F);
		player.setLevel(0);
		player.setAllowFlight(false);
		player.setFlying(false);
		player.setWalkSpeed(0.2f);
		player.spigot().setCollidesWithEntities(true);
		player.setGameMode(GameMode.SURVIVAL);
		player.getInventory().setArmorContents(new ItemStack[4]);
		player.getInventory().setContents(new ItemStack[36]);
		for (PotionEffect activePotionEffect : player.getActivePotionEffects()) {
			player.removePotionEffect(activePotionEffect.getType());
		}

		if (resetHeldSlot) {
			player.getInventory().setHeldItemSlot(0);
		}

		player.updateInventory();
	}

	public void denyMovement(Player player) {
		player.setFlying(false);
		player.setWalkSpeed(0.0F);
		player.setFoodLevel(0);
		player.setSprinting(false);
		player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 200));
	}

	public void allowMovement(Player player) {
		player.setFlying(false);
		player.setWalkSpeed(0.2F);
		player.setFoodLevel(20);
		player.setSprinting(true);
		player.removePotionEffect(PotionEffectType.JUMP);
	}

	public PlayerVersion getPlayerVersion(Player player) {
		return PlayerVersionHandler.version.getPlayerVersion(player);
	}

	public int getStaffCount() {
		int count = 0;
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (Profile.get(onlinePlayer.getUniqueId()).getState() == ProfileState.STAFF_MODE) count++;
		}
		return count;
	}
}
