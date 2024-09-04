package me.hcfalerts.practice.clan.commands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.clan.commands.subcommands.*;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class ClanCommand extends BaseCommand {

    public ClanCommand() {
        new ClanCreateCommand();
        new ClanDisbandCommand();
        new ClanInfoCommand();
        new ClanListCommand();
        new ClanJoinCommand();
        new ClanInviteCommand();
        new ClanRenameCommand();
        new ClanKickCommand();
        new ClanSetColorCommand();
        new ClanLeaveCommand();
        new ClanSetPointsCommand();
        new ClanChatCommand();
    }

    @Command(name = "clan")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        new MessageFormat(Locale.CLAN_HELP
                .format(Profile.get(player.getUniqueId()).getLocale()))
                .send(player);
    }
}
