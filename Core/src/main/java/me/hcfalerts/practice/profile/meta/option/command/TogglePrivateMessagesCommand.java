package me.hcfalerts.practice.profile.meta.option.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class TogglePrivateMessagesCommand extends BaseCommand {

    @Command(name = "togglepm", aliases = {"togglepms", "tpm", "tpms"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        profile.getOptions().receivingNewConversations(!profile.getOptions().receivingNewConversations());
        profile.getConversations().expireAllConversations();

        if (profile.getOptions().receivingNewConversations()) {
            new MessageFormat(Locale.OPTIONS_PRIVATE_MESSAGES_ENABLED
                    .format(profile.getLocale()))
                    .send(player);
        } else {
            new MessageFormat(Locale.OPTIONS_PRIVATE_MESSAGES_DISABLED
                    .format(profile.getLocale()))
                    .send(player);
        }
    }
}
