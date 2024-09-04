package me.hcfalerts.practice.profile.conversation.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.conversation.Conversation;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageCommand extends BaseCommand {

    @Command(name = "message", aliases = {"msg", "whisper", "tell", "t"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length < 2) {
            player.sendMessage(CC.RED + "Please usage: /" + commandArgs.getLabel() + " (player) (message)");
            return;
        }

        StringBuilder message = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (player.equals(target)) {
            player.sendMessage(CC.RED + "You cannot message yourself!");
            return;
        }

        if (target == null) {
            new MessageFormat(Locale.PLAYER_NOT_FOUND
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }

        Profile playerProfile = Profile.get(player.getUniqueId());

        //if (targetProfile.getConversations().canBeMessagedBy(player)) {
        Conversation conversation = playerProfile.getConversations().getOrCreateConversation(target);

        if (conversation.validate()) {
            conversation.sendMessage(player, target, message.toString());
        } else {
            player.sendMessage(CC.RED + "That player is not receiving new conversations right now.");
        }
/*        } else {
            player.sendMessage(CC.RED + "That player is not receiving new conversations right now.");
        }*/
    }
}
