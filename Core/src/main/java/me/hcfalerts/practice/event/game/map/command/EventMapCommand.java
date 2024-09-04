package me.hcfalerts.practice.event.game.map.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EventMapCommand extends BaseCommand {

    private final String[][] HELP = new String[][] {
            new String[]{ "/event map create", "Create a event map" },
            new String[]{ "/event map delete", "Delete a event map" },
            new String[]{ "/event map addspawn", "Add spawn to event map" },
            new String[]{ "/event map status", "Get status of event map" }
    };

    public EventMapCommand() {
        super();
        new EventMapCreateCommand();
        new EventMapDeleteCommand();
        new EventMapSetSpawnCommand();
        new EventMapStatusCommand();
    }

    @Command(name = "event.map", permission = "practice.event.admin")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.PINK + CC.BOLD + "Event Map Help " + CC.GRAY + "(1/1)");

        for (String[] command : HELP) {
            player.sendMessage(CC.PINK + command[0] + CC.GRAY + " - " + CC.WHITE + command[1]);
        }

        player.sendMessage(CC.CHAT_BAR);
    }
}
