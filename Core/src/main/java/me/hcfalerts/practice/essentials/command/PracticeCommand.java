package me.hcfalerts.practice.essentials.command;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.TPSUtil;
import me.hcfalerts.practice.utilities.string.UUIDFetcher;
import org.bukkit.entity.Player;

public class PracticeCommand extends BaseCommand {

    @Command(name = "practice")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("reload") && player.hasPermission("practice.reload")) {
                long start = System.currentTimeMillis();
                HCFPractice.get().getMainConfig().reload();
                HCFPractice.get().getLang().reload();
                HCFPractice.get().getLangConfig().reload();
                HCFPractice.get().getHotbarConfig().reload();
                HCFPractice.get().getArenasConfig().reload();
                HCFPractice.get().getEventsConfig().reload();
                HCFPractice.get().getKitsConfig().reload();
                HCFPractice.get().getScoreboardConfig().reload();
                HCFPractice.get().getTabLobbyConfig().reload();
                HCFPractice.get().getTabSingleFFAFightConfig().reload();
                HCFPractice.get().getTabSingleTeamFightConfig().reload();
                HCFPractice.get().getTabPartyFFAFightConfig().reload();
                HCFPractice.get().getTabPartyTeamFightConfig().reload();
                HCFPractice.get().getTabEventConfig().reload();
                HCFPractice.get().getOptionsConfig().reload();
                HCFPractice.get().getDeathEffectsInvConfig().reload();
                HCFPractice.get().getDiscordWebhookConfig().reload();
                //HCFPractice.get().getEssentials().setMotd(CC.translate(HCFPractice.get().getLangConfig().getStringList("MOTD")));
                long finish = System.currentTimeMillis();
                player.sendMessage(CC.translate("&dPractice reloaded &7(" + (finish - start) + "ms)"));
                return;
            }
            else if (args[0].equalsIgnoreCase("information") || args[0].equalsIgnoreCase("info")
                    || args[0].equalsIgnoreCase("dev")) {
                try {
                    if (UUIDFetcher.getUUIDOf("HCFAlerts").equals(player.getUniqueId())) {
                        player.sendMessage(CC.CHAT_BAR);
                        player.sendMessage(CC.translate("&dThis server is running &lHCFPractice &d" + HCFPractice.get().getDescription().getVersion() + " &dat &7[" + TPSUtil.getCoolestTPS(20) + "&7] (&d" + TPSUtil.getTPS() + "&7) &dTPS"));
                        player.sendMessage(CC.CHAT_BAR);
                        return;
                    }
                } catch (Exception e) {
                    player.sendMessage(CC.CHAT_BAR);
                    player.sendMessage(CC.translate("&dThis server is running &lHCFPractice &d" + HCFPractice.get().getDescription().getVersion() + " &dat &7[" + TPSUtil.getCoolestTPS(20) + "&7] (&d" + TPSUtil.getTPS() + "&7) &dTPS"));
                    player.sendMessage(CC.CHAT_BAR);
                }
            }
            return;
        }

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&dThis server is running &lHCFPractice &d" + HCFPractice.get().getDescription().getVersion() + " &dat &7[" + TPSUtil.getCoolestTPS(20) + "&7] (&d" + TPSUtil.getTPS() + "&7) &dTPS"));
        player.sendMessage(CC.CHAT_BAR);
    }
}