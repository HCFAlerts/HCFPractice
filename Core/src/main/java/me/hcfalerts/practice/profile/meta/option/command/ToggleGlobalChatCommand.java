package me.hcfalerts.practice.profile.meta.option.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class ToggleGlobalChatCommand extends BaseCommand {

    @Command(name = "toggleglobalchat", aliases = {"tgc", "togglepublicchat", "tpc"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        profile.getOptions().publicChatEnabled(!profile.getOptions().publicChatEnabled());

        if (profile.getOptions().publicChatEnabled()) {
            new MessageFormat(Locale.OPTIONS_GLOBAL_CHAT_ENABLED
                    .format(profile.getLocale()))
                    .send(player);
        } else {
            new MessageFormat(Locale.OPTIONS_GLOBAL_CHAT_DISABLED
                    .format(profile.getLocale()))
                    .send(player);
        }
    }
}
