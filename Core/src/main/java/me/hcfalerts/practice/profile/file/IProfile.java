package me.hcfalerts.practice.profile.file;

import me.hcfalerts.practice.profile.Profile;

public interface IProfile {

    void save(Profile profile);

    void load(Profile profile);
}
