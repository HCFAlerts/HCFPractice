package me.hcfalerts.practice.profile.deatheffects.commands;

import me.hcfalerts.practice.profile.deatheffects.menu.DeathEffectMenu;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class DeathEffectCommand extends BaseCommand {

    @Command(name = "deatheffect", permission = "practice.command.deatheffects")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();

        new DeathEffectMenu().openMenu(player);
    }
}
