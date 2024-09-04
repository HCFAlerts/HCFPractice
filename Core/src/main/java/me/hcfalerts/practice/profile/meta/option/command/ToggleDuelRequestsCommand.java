package me.hcfalerts.practice.profile.meta.option.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class ToggleDuelRequestsCommand extends BaseCommand {

    @Command(name = "toggleduels", aliases = {"tgr", "tgd"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        profile.getOptions().receiveDuelRequests(!profile.getOptions().receiveDuelRequests());

        if (profile.getOptions().receiveDuelRequests()) {
            new MessageFormat(Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_ENABLED
                    .format(profile.getLocale()))
                    .send(player);
        } else {
            new MessageFormat(Locale.OPTIONS_RECEIVE_DUEL_REQUESTS_DISABLED
                    .format(profile.getLocale()))
                    .send(player);
        }
    }
}
