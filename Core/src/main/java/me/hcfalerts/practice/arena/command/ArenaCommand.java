package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.command.CommandSender;


public class ArenaCommand extends BaseCommand {

    public ArenaCommand() {
        super();
        new ArenaAddKitCommand();
        new ArenaCreateCommand();
        new ArenaDeleteCommand();
        new ArenaGenerateCommand();
        new ArenaGenHelperCommand();
        new ArenaRemoveKitCommand();
        new ArenaSaveCommand();
        new ArenaSelectionCommand();
        new ArenaSetSpawnCommand();
        new ArenaStatusCommand();
        new ArenaSetAuthorCommand();
        new ArenaTeleportCommand();
    }

    @Command(name = "arena", permission = "practice.arena.admin", inGameOnly = false)
    @Override
    public void onCommand(CommandArgs commandArgs) {
        CommandSender sender = commandArgs.getSender();

        sender.sendMessage(CC.CHAT_BAR);
        sender.sendMessage(CC.translate("&d&lArena Help &7(1/1)"));
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate("&a● &7Needed to setup Arena"));
        sender.sendMessage(CC.translate("&6● &7Recommended to setup Arena"));
        sender.sendMessage(CC.translate("&c● &7Not Needed to setup Arena"));
        sender.sendMessage(CC.translate(""));
        sender.sendMessage(CC.translate("&a● &f/arena wand"));
        sender.sendMessage(CC.translate("&a● &f/arena create <arena_name> <SHARED/STANDALONE>"));
        sender.sendMessage(CC.translate("&a● &f/arena setspawn <arena_name> <a|b|red|blue>"));
        sender.sendMessage(CC.translate("&a● &f/arena addkit <arena_name> <kit_name>"));
        sender.sendMessage(CC.translate("&6● &f/arena save"));
        sender.sendMessage(CC.translate("&6● &f/arena generate <arena_name>"));
        sender.sendMessage(CC.translate("&6● &f/arena genhelper"));
        sender.sendMessage(CC.translate("&c● &f/arena removekit <arena_name> <kit_name>"));
        sender.sendMessage(CC.translate("&c● &f/arena delete <arena_name>"));
        sender.sendMessage(CC.translate("&c● &f/arena status <arena_name>"));
        sender.sendMessage(CC.translate("&c● &f/arena setauthor <arena_name> <author_name>"));
        sender.sendMessage(CC.translate("&c● &f/arena teleport <arena_name>"));
        sender.sendMessage(CC.translate("&c● &f/arenas"));
        sender.sendMessage(CC.CHAT_BAR);
    }
}
