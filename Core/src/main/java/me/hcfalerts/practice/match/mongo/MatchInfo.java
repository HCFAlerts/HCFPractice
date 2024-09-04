package me.hcfalerts.practice.match.mongo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.hcfalerts.practice.kit.Kit;

@Setter @Getter
@RequiredArgsConstructor
public class MatchInfo {

    private final String winningParticipant;
    private final String losingParticipant;
    private final Kit kit;
    private final int newWinnerElo;
    private final int newLoserElo;
    private final String date;
    private final String duration;
}