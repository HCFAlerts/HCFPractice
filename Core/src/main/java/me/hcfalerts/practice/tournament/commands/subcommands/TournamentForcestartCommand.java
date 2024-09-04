package me.hcfalerts.practice.tournament.commands.subcommands;

import com.google.common.collect.Maps;
import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.tournament.Tournament;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class TournamentForcestartCommand extends BaseCommand {

    @Command(name = "tournament.forcestart", permission = "practice.command.tournament.forcestart")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        if (Tournament.getTournament() == null) {
            new MessageFormat(Locale.TOURNAMENT_NO_FOUND
                    .format(Profile.get(player.getUniqueId()).getLocale()))
                    .send(player);
            return;
        }

        if (Tournament.getTournament().getCountdown() != null) Tournament.getTournament().getCountdown().stop();
        Tournament.getTournament().broadcast(Locale.TOURNAMENT_FORCE_START, Maps.newHashMap());
        Tournament.getTournament().start();
    }
}
