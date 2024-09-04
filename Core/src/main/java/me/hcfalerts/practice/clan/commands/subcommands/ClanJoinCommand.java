package me.hcfalerts.practice.clan.commands.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class ClanJoinCommand extends BaseCommand {

    @Command(name = "clan.join")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        Clan clan = Clan.getByName(args[0]);
        if (clan == null) {
            player.sendMessage(CC.RED + "Please insert a valid Clan.");
            return;
        }

        Profile profile = Profile.get(player.getUniqueId());
        if (profile.getClan() != null) {
            new MessageFormat(Locale.CLAN_ERROR_PLAYER_ALREADY_IN_CLAN
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        if (!profile.getInvites().containsKey(clan.getName())) {
            new MessageFormat(Locale.CLAN_ERROR_NOT_INVITATION
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }
        if (profile.getInvites().get(clan.getName()).isExpired()) {
            new MessageFormat(Locale.CLAN_ERROR_INVITATION_EXPIRED
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        clan.join(player);
    }
}
