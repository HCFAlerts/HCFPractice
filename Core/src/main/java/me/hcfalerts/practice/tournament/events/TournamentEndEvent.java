package me.hcfalerts.practice.tournament.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.hcfalerts.practice.match.participant.MatchGamePlayer;
import me.hcfalerts.practice.match.participant.GameParticipant;
import me.hcfalerts.practice.utilities.event.CustomEvent;

@Getter @Setter
@AllArgsConstructor
public class TournamentEndEvent extends CustomEvent {

    private final GameParticipant<MatchGamePlayer> winner;
    private final boolean team;
    private final boolean clan;

}