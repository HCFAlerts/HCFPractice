package me.hcfalerts.practice.match.command;

import me.hcfalerts.practice.Locale;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;
import me.hcfalerts.practice.utilities.command.BaseCommand;
import me.hcfalerts.practice.utilities.command.Command;
import me.hcfalerts.practice.utilities.command.CommandArgs;
import me.hcfalerts.practice.utilities.string.MessageFormat;
import org.bukkit.entity.Player;

public class RallyCommand extends BaseCommand {

    @Command(name = "rally", aliases = {"f.rally"})
    @Override
    public void onCommand(CommandArgs command) {
        Player player = command.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());

        if (profile.getState() == ProfileState.FIGHTING) {
            Match match = profile.getMatch();

            if (match.getKit().getGameRules().isHcf()) {
                GameParticipant<MatchGamePlayer> participant = match.getParticipant(player);
                participant.setRally(player.getLocation());
                participant.sendRallyWaypoints();
                new MessageFormat(Locale.DUEL_RALLY_ACTIVATE_MESSAGE.format(profile.getLocale())).send(player);
            } else {
                new MessageFormat(Locale.DUEL_NOT_HCF_KIT.format(profile.getLocale())).send(player);
            }
        } else {
            new MessageFormat(Locale.DUEL_NOT_IN_MATCH.format(profile.getLocale())).send(player);
        }
    }
}
