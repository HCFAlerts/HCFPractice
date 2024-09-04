package me.hcfalerts.practice.clan.commands.subcommands;

import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class ClanListCommand extends BaseCommand {

    @Command(name = "clan.list")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        player.sendMessage(CC.translate("&6&lList of all Clans"));
        for (Clan value : Clan.getClans().values()) {
            player.sendMessage(CC.translate("&7- &e" + StringUtils.capitalize(value.getName().toLowerCase())));
        }
    }
}
