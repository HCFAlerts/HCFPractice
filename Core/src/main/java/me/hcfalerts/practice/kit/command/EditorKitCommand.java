package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.kit.menu.edit.KitEditorSelectKitMenu;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class EditorKitCommand extends BaseCommand {

    @Command(name = "editorkit")
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.getState() == ProfileState.LOBBY || profile.getState() == ProfileState.QUEUEING) {
            new KitEditorSelectKitMenu().openMenu(player);
        }
    }
}
