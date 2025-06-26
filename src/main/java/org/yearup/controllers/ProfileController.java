package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.Profile;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("/profile")
@CrossOrigin
public class ProfileController
{
    private final ProfileDao profileDao;
    private final UserDao userDao;

    @Autowired
    public ProfileController(ProfileDao profileDao, UserDao userDao)
    {
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    // Giriş yapan kullanıcının profili
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Profile getProfile(Principal principal)
    {
        String username = principal.getName();           // Token'dan gelen username
        User user = userDao.getByUserName(username);     // userId'yi alıyoruz
        if (user == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        Profile profile = profileDao.getByUserId(user.getId());

        if (profile == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found.");
        }

        return profile;
    }

    // Yeni profil oluşturma
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public Profile createProfile(@RequestBody Profile profile, Principal principal)
    {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        int userId = user.getId();
        profile.setUserId(userId);

        Profile existing = profileDao.getByUserId(userId);
        if (existing != null)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile already exists.");
        }

        return profileDao.create(profile);
    }
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public Profile updateProfile(@RequestBody Profile updatedProfile, Principal principal)
    {
        String username = principal.getName();
        User user = userDao.getByUserName(username);

        if (user == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.");
        }

        Profile existing = profileDao.getByUserId(user.getId());
        if (existing == null)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found.");
        }

        updatedProfile.setUserId(user.getId());

        return profileDao.update(updatedProfile);
    }

}
