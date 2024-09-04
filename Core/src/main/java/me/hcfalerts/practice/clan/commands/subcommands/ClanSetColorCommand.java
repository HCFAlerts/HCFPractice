package me.hcfalerts.practice.clan.commands.subcommands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.clan.Clan;
import me.hcfalerts.practice.nametags.NameTag;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.utilities.TaskUtil;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.chat.StyleUtil;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ClanSetColorCommand extends BaseCommand {

    @Command(name = "clan.setcolor")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        if (args.length == 0) {
            player.sendMessage(CC.RED + "Please insert color.");
            return;
        }

        String color = args[0];
        Profile profile = Profile.get(player.getUniqueId());
        Clan clan = profile.getClan();
        if (clan == null) {
            new MessageFormat(Locale.CLAN_ERROR_PLAYER_NOT_FOUND
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        if (!player.getUniqueId().equals(profile.getClan().getLeader())) {
            new MessageFormat(Locale.CLAN_ERROR_ONLY_OWNER
                    .format(profile.getLocale()))
                    .send(player);
            return;
        }

        ChatColor chatColor = CC.getByName(color);
        if (chatColor == null || !chatColor.isColor()) {
            player.sendMessage(CC.RED + "Invalid color.");
            return;
        }

        clan.setColor(chatColor);
        clan.save();
        clan.broadcast(Locale.CLAN_SET_COLOR_BROADCAST, new MessageFormat()
                .add("{new_color}", StyleUtil.colorName(ChatColor.valueOf(color.toUpperCase())))
                .add("{color}", color.toUpperCase()));
        TaskUtil.runAsync(() -> {
            NameTag.reloadOthersFor(player);
            NameTag.reloadPlayer(player);
        });
    }
}