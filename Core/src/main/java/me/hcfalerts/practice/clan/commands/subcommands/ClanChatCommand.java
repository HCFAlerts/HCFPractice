package me.hcfalerts.practice.clan.commands.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class ClanChatCommand extends BaseCommand {

    @Command(name = "clan.chat", aliases = {"clan.c"})
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.getClan() == null) {
            new MessageFormat(Locale.CLAN_DO_NOT_HAVE
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        String[] args = commandArgs.getArgs();
        StringBuilder message = new StringBuilder();

        if (args.length == 0) {
            new MessageFormat(Locale.CLAN_INSERT_A_MESSAGE
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        for (String msg : args) message.append(msg).append(" ");

        profile.getClan().chat(player, message.toString());
    }
}
