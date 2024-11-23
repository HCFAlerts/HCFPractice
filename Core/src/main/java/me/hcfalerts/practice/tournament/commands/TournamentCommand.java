package me.hcfalerts.practice.tournament.commands;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.tournament.commands.subcommands.TournamentForcestartCommand;
import me.hcfalerts.practice.tournament.commands.subcommands.TournamentJoinCommand;
import me.hcfalerts.practice.tournament.commands.subcommands.TournamentStartCommand;
import me.hcfalerts.practice.tournament.commands.subcommands.TournamentStopCommand;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class TournamentCommand extends BaseCommand {

    public TournamentCommand() {
        new TournamentJoinCommand();
        new TournamentStartCommand();
        new TournamentStopCommand();
        new TournamentForcestartCommand();
    }

    @Command(name = "tournament")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        new MessageFormat(Locale.TOURNAMENT_HELP.format(Profile.get(player.getUniqueId()).getLocale())).send(player);
    }
}