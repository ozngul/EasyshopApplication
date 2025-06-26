package org.yearup.data;

import org.yearup.models.Profile;

public interface ProfileDao {
    Profile create(Profile profile);      // ← bu şekilde olmalı
    void deleteByUserId(int userId);
    Profile getByUserId(int userId);
    Profile update(Profile profile);

}
