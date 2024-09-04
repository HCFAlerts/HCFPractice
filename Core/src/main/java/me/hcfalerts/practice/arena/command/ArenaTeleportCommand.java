package me.hcfalerts.practice.arena.command;

import me.hcfalerts.practice.arena.Arena;
import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class ArenaTeleportCommand extends BaseCommand {

    @Command(name = "arena.teleport", permission = "practice.arena.admin")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();
        String[] args = commandArgs.getArgs();

        Arena arena = Arena.getByName(args[0]);
        if (arena == null) {
            player.sendMessage(CC.RED + "An arena with that name does not exist.");
            return;
        }

        player.teleport(arena.getSpawnA());
        player.sendMessage(CC.translate("&aYou have been teleported to &f" + arena.getName() + " &aArena."));
    }
}