package me.hcfalerts.practice.tournament.commands;

import me.hcfalerts.practice.tournament.commands.subcommands.TournamentForcestartCommand;
import me.hcfalerts.practice.tournament.commands.subcommands.TournamentJoinCommand;
import me.hcfalerts.practice.tournament.commands.subcommands.TournamentStartCommand;
import me.hcfalerts.practice.tournament.commands.subcommands.TournamentStopCommand;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
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

        player.sendMessage(CC.translate("&e&lTournament Help"));
        player.sendMessage(CC.translate("&7/tournament start (kit) (size) (limit) [true/false]"));
        player.sendMessage(CC.translate("&7/tournament forcestart"));
        player.sendMessage(CC.translate("&7/tournament stop"));
        player.sendMessage(CC.translate("&7/tournament join"));
    }
}