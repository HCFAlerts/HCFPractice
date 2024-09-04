package me.hcfalerts.practice.utilities.rank;

import lombok.Getter;
import me.hcfalerts.practice.utilities.ClassHelper;
import me.hcfalerts.practice.utilities.rank.impl.Default;
import org.bukkit.Bukkit;

public class RankManager {

    @Getter private IRank rank;

    public RankManager() {
        verifyRank();
    }

    public void verifyRank() {
        boolean foundRank = false;

        for (Class<?> aClass : ClassHelper.getClassesInPackage("me.hcfalerts.practice.utilities.rank.impl")) {
            String className = aClass.getSimpleName();

            if (Bukkit.getPluginManager().getPlugin(className) != null) {
                try {
                    this.rank = (IRank) aClass.newInstance();
                    foundRank = true;
                    break;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (!foundRank) this.rank = new Default();
    }
}
