package me.hcfalerts.practice.match.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.hcfalerts.practice.match.Match;
import me.hcfalerts.practice.utilities.event.CustomEvent;

@AllArgsConstructor
@Getter
public class MatchEndEvent extends CustomEvent {
    private final Match match;
}