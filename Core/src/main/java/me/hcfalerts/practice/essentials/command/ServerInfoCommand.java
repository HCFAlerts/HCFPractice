package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.arena.ArenaType;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.TPSUtil;
import org.bukkit.entity.Player;

public class ServerInfoCommand extends BaseCommand {

    @Command(name = "serverinfo", permission = "practice.admin")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&d&lServer Information"));
        player.sendMessage("");
        player.sendMessage(CC.translate("&5Arenas"));
        player.sendMessage("");
        player.sendMessage(CC.translate("&dStandalone&7: &f" + getStandaloneSize() + " &7(&c" + getStandaloneAvailable() + "&7)"));
        player.sendMessage(CC.translate("&dShared&7: &f" + getSharedSize()));
        player.sendMessage("");
        player.sendMessage(CC.translate("&5Performance"));
        player.sendMessage(CC.translate("&7[" + TPSUtil.getCoolestTPS(20) + "&7]"));
        player.sendMessage(CC.translate("&dFree Memory&7: &f" + getFreeMemory()));
        player.sendMessage(CC.CHAT_BAR);
    }

    private int getStandaloneAvailable() {
        int count = 0;
        for (Arena arena : Arena.getArenas()) {
            if (!arena.isBusy() && arena.isSetup() && arena.getType() == ArenaType.STANDALONE) {
                count++;
            }
        }
        return count;
    }

    private int getStandaloneSize() {
        int count = 0;
        for (Arena arena : Arena.getArenas()) {
            if (arena.isSetup() && arena.getType() == ArenaType.STANDALONE) {
                count++;
            }
        }
        return count;
    }

    private int getSharedSize() {
        int count = 0;
        for (Arena arena : Arena.getArenas()) {
            if (arena.isSetup() && arena.getType() == ArenaType.SHARED) {
                count++;
            }
        }
        return count;
    }

    private String getFreeMemory() {
        return Runtime.getRuntime().freeMemory() / 1048576 + " MB";
    }
}
