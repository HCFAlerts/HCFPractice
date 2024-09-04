package me.hcfalerts.practice.clan;

import me.hcfalerts.practice.HCFPractice;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.tournament.events.TournamentEndEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ClanListener implements Listener {

    @EventHandler
    public void onClanWinTournament(TournamentEndEvent event){
        if(!event.isClan()) return;
        MatchGamePlayer leader = event.getWinner().getLeader();
        Player player = leader.getPlayer();
        Profile profile = Profile.get(player.getUniqueId());
        Clan clan = profile.getClan();
        clan.addPoints(HCFPractice.get().getMainConfig().getInteger("WINNING-POINTS-CLAN-TOURNAMENT"));
    }
}