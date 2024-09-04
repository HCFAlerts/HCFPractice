package me.hcfalerts.practice.match.command;

import me.hcfalerts.practice.kit.Kit;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.queue.Queue;
import me.hcfalerts.practice.queue.QueueProfile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ForceQueueCommand extends BaseCommand {

    @Command(name = "forcequeue", aliases = {"fm"}, permission = "practice.command.forcequeue", inGameOnly = false)
    @Override
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();
        String label = commandArgs.getLabel();
        String[] args = commandArgs.getArgs();

        if (args.length < 3) {
            sender.sendMessage(CC.translate("&cUsage: /" + label + " <player> <unranked/ranked> <kit>"));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(CC.translate("&cPlayer not found or not online"));
            return;
        }

        String type = args[1];
        if (!type.equalsIgnoreCase("unranked") && !type.equalsIgnoreCase("ranked")) {
            sender.sendMessage(CC.translate("&cType must be unranked or ranked"));
            return;
        }
        boolean ranked = args[1].equalsIgnoreCase("ranked");

        Kit kit = Kit.getByName(args[2]);
        if (kit == null) {
            sender.sendMessage(CC.translate("&cKit not found"));
            return;
        }

        if (Queue.getQueueProfileByUuid(player.getUniqueId()) != null) {
            QueueProfile profile = Queue.getQueueProfileByUuid(player.getUniqueId());
            if (profile != null) {
                Queue queue = profile.getQueue();
                queue.removePlayer(profile);
            }
        }

        for (Queue queue : Queue.getQueues()) {
            if (queue.getKit() == kit && queue.isRanked() == ranked) {
                queue.addPlayer(player, ranked ? Profile.get(player.getUniqueId()).getKitData().get(kit).getElo() : 0);
            }
        }

        sender.sendMessage(CC.translate("&aForced " + player.getName() + " to queue"));
    }
}
