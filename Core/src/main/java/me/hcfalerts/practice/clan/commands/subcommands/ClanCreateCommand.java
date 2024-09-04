package me.hcfalerts.practice.clan.commands.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.nametags.NameTag;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClanCreateCommand extends BaseCommand {

    @Command(name = "clan.create")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        String clanName = args[0];
        Profile profile = Profile.get(player.getUniqueId());
        if (profile.getClan() != null) {
            new MessageFormat(Locale.CLAN_ERROR_PLAYER_ALREADY_IN_CLAN
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }
        String deColored = ChatColor.stripColor(clanName);
        if (Clan.getByName(deColored) != null) {
            new MessageFormat(Locale.CLAN_ERROR_ALREADY_EXIST
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }
        if (deColored.length() > 5 || deColored.length() < 2) {
            new MessageFormat(Locale.CLAN_ERROR_MAX_LENGTH_NAME
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }
        Clan clan = new Clan(deColored, player.getUniqueId());
        Clan.getClans().put(deColored, clan);
        profile.setClan(clan);
        clan.getMembers().add(player.getUniqueId());
        new MessageFormat(Locale.CLAN_CREATE.format(profile.getLocale()))
                .add("{name}", deColored)
                .send(player);
        //player.sendMessage(translate("&eThe " + deColored + " clan has been successfully created."));
        clan.save();
        TaskUtil.runAsync(() -> {
            NameTag.reloadOthersFor(player);
            NameTag.reloadPlayer(player);
        });
    }
}
