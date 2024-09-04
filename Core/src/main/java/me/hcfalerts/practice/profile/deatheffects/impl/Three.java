package me.hcfalerts.practice.profile.deatheffects.impl;

import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.deatheffects.Data;
import me.hcfalerts.practice.profile.deatheffects.util.ParticleEffect;
import me.hcfalerts.practice.utilities.ItemBuilder;
import me.hcfalerts.practice.utilities.TaskUtil;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Three extends Data {

    private final ParticleEffect effect = ParticleEffect.REDSTONE;

    public Three(Profile profile) {
        super(profile);
    }

    @Override
    public ItemStack getItemStack() {
        return new ItemBuilder(Material.CHEST).build();
    }

    @Override
    public void apply() {
        Player player = getProfile().getPlayer();
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                runHelix(player.getLocation());
            }
        };
        TaskUtil.runTimerAsync(runnable, 0L, 10L);
        setTaskId(runnable.getTaskId());
    }

    /*
     I used this help for me https://bukkit.org/threads/shaped-particle-effects.303388/
     */
    public void runHelix(Location loc) {
        double radius;

        for (double y = 5; y >= 0; y -= 0.007) {
            radius = y / 3;
            double x = radius * Math.cos(3 * y);
            double z = radius * Math.sin(3 * y);

            double y2 = 5 - y;

            Location loc2 = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y2, loc.getZ() + z);
            effect.display(0, 0, 0, 0, 1, loc2, 20);
        }

        for (double y = 5; y >= 0; y -= 0.007) {
            radius = y / 3;
            double x = -(radius * Math.cos(3 * y));
            double z = -(radius * Math.sin(3 * y));

            double y2 = 5 - y;

            Location loc2 = new Location(loc.getWorld(), loc.getX() + x, loc.getY() + y2, loc.getZ() + z);
            effect.display(0, 0, 0, 0, 1, loc2, 20);
        }

    }
}
