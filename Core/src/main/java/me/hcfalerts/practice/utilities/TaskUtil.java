package me.hcfalerts.practice.utilities;

import lombok.experimental.UtilityClass;
import me.hcfalerts.practice.HCFPractice;
import org.bukkit.plugin.IllegalPluginAccessException;
import org.bukkit.scheduler.BukkitRunnable;

@UtilityClass
public class TaskUtil {

    public void run(Runnable runnable) {
        HCFPractice.get().getServer().getScheduler().runTask(HCFPractice.get(), runnable);
    }

    public void runAsync(Runnable runnable) {
        try {
            HCFPractice.get().getServer().getScheduler().runTaskAsynchronously(HCFPractice.get(), runnable);
        } catch (IllegalStateException e) {
            HCFPractice.get().getServer().getScheduler().runTask(HCFPractice.get(), runnable);
        } catch (IllegalPluginAccessException e) {
            new Thread(runnable).start();
        }
        catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void runTimer(Runnable runnable, long delay, long timer) {
        HCFPractice.get().getServer().getScheduler().runTaskTimer(HCFPractice.get(), runnable, delay, timer);
    }

    public int runTimer(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimer(HCFPractice.get(), delay, timer);
        return runnable.getTaskId();
    }

    public void runLater(Runnable runnable, long delay) {
        HCFPractice.get().getServer().getScheduler().runTaskLater(HCFPractice.get(), runnable, delay);
    }

    public void runLaterAsync(Runnable runnable, long delay) {
        try {
            HCFPractice.get().getServer().getScheduler().runTaskLaterAsynchronously(HCFPractice.get(), runnable, delay);
        } catch (IllegalStateException e) {
            HCFPractice.get().getServer().getScheduler().runTaskLater(HCFPractice.get(), runnable, delay);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void runTimerAsync(Runnable runnable, long delay, long timer) {
        try {
            HCFPractice.get().getServer().getScheduler().runTaskTimerAsynchronously(HCFPractice.get(), runnable, delay, timer);
        } catch (IllegalStateException e) {
            HCFPractice.get().getServer().getScheduler().runTaskTimer(HCFPractice.get(), runnable, delay, timer);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public void runTimerAsync(BukkitRunnable runnable, long delay, long timer) {
        runnable.runTaskTimerAsynchronously(HCFPractice.get(), delay, timer);
    }

}
