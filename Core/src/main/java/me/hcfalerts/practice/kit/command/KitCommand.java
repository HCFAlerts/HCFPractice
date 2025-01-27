package me.hcfalerts.practice.kit.command;

import me.hcfalerts.practice.utilities.chat.CC;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import org.bukkit.entity.Player;

public class KitCommand extends BaseCommand {

    public KitCommand() {
        super();
        new KitCreateCommand();
        new KitSetRuleCommand();
        new KitGetLoadoutCommand();
        new KitSetLoadoutCommand();
        new KitSetIconCommand();
        new KitToggleComand();
        new KitDeleteCommand();
        new KitStatusCommand();
        new KitRulesCommand();
        new KitSetRankedSlotCommand();
        new KitSetUnRankedSlotCommand();
        new KitRenameCommand();
        new KitEditorCommand();
        new KitArenasCommand();
    }

    @Command(name = "kit", permission = "practice.kit.admin")
    @Override
    public void onCommand(CommandArgs commandArgs) {
        Player player = commandArgs.getPlayer();

        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.translate("&d&lKits Help &7(1/1)"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&a● &fNeeded to setup Kit"));
        player.sendMessage(CC.translate("&6● &fRecommended to customize Kit"));
        player.sendMessage(CC.translate("&c● &fNot Needed to setup Kit"));
        player.sendMessage(CC.translate(""));
        player.sendMessage(CC.translate("&a● &f/kit create <kit_name>"));
        player.sendMessage(CC.translate("&a● &f/kit editor"));
        player.sendMessage(CC.translate("&a● &f/kit setloadout <kit_name>"));
        player.sendMessage(CC.translate("&a● &f/kit toggle <kit_name>"));
        player.sendMessage(CC.translate("&a● &f/kit setrule <kit_name> <rule> <value>"));
        player.sendMessage(CC.translate("&6● &f/kit seticon <kit_name>"));;
        player.sendMessage(CC.translate("&6● &f/kit setrankedslot <kit_name> <slot>"));
        player.sendMessage(CC.translate("&6● &f/kit setunrankedslot (<kit_name> <slot>"));
        player.sendMessage(CC.translate("&c● &f/kit delete <kit_name>"));
        player.sendMessage(CC.translate("&c● &f/kit getloadout <kit_name>"));
        player.sendMessage(CC.translate("&c● &f/kit rules"));
        player.sendMessage(CC.translate("&c● &f/kit rename <kit_name> <new_name>"));
        player.sendMessage(CC.translate("&c● &f/kit status <kit_name>"));
        player.sendMessage(CC.translate("&c● &f/kits"));
        player.sendMessage(CC.CHAT_BAR);
    }
}
