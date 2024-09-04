package me.hcfalerts.practice;

import me.hcfalerts.practice.knockback.Knockback;
import me.hcfalerts.practice.knockback.KnockbackProfiler;
import me.hcfalerts.practice.profile.Profile;
import me.hcfalerts.practice.profile.ProfileState;

import java.util.UUID;

public class PracticeAPI {

    public static void setKnockbackProfile(KnockbackProfiler profile) {
        Knockback.setKnockbackProfiler(profile);
    }

    public boolean isInQueue(UUID uuid) {
        return Profile.get(uuid).getState() == ProfileState.QUEUEING;
    }

    public boolean isInMatch(UUID uuid) {
        return Profile.get(uuid).getState() == ProfileState.FIGHTING;
    }

    public boolean isInStaffMode(UUID uuid) {
        return Profile.get(uuid).getState() == ProfileState.STAFF_MODE;
    }

    public boolean isInSpectating(UUID uuid) {
        return Profile.get(uuid).getState() == ProfileState.SPECTATING;
    }

    public boolean isInEvent(UUID uuid) {
        return Profile.get(uuid).getState() == ProfileState.EVENT;
    }

    public boolean isInLobby(UUID uuid) {
        return Profile.get(uuid).getState() == ProfileState.LOBBY;
    }

}
