package me.hcfalerts.practice.clan.commands.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class ClanLeaveCommand extends BaseCommand {

    @Command(name = "clan.leave")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        Profile profile = Profile.get(player.getUniqueId());
        Clan clan = profile.getClan();
        if (clan == null) {
            new MessageFormat(Locale.CLAN_ERROR_PLAYER_NOT_FOUND
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        if (player.getUniqueId().equals(clan.getLeader())) {
            new MessageFormat(Locale.CLAN_ERROR_OWNER_LEAVE
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        profile.setClan(null);
        clan.getMembers().remove(player.getUniqueId());
        new MessageFormat(Locale.CLAN_LEAVE_PLAYER
                .format(profile.getLocale()))
                .send(player);
        for (Player onPlayer : clan.getOnPlayers()) {
            new MessageFormat(Locale.CLAN_LEAVE_BROADCAST.format(Profile.get(onPlayer.getUniqueId()).getLocale()))
                    .add("{player_name}", player.getName())
                    .send(onPlayer);
        }

        TaskUtil.runAsync(profile::save);
    }
}