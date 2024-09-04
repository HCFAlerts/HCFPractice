package me.hcfalerts.practice.profile.deatheffects;

import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

@Setter @Getter
public abstract class Data {

    private final Profile profile;
    private int taskId = 0;
    private ItemStack itemStack;

    public Data(Profile profile) {
        this.profile = profile;
    }

    public void apply() {

    }

    public void stop() {
        if (taskId != 0) {
            Bukkit.getServer().getScheduler().cancelTask(taskId);
            taskId = 0;
        }
    }
}
