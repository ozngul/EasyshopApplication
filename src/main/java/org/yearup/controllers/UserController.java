package org.yearup.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.yearup.data.ProfileDao;
import org.yearup.data.UserDao;
import org.yearup.models.User;

@RestController
@RequestMapping("/users")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserDao userDao;
    private final ProfileDao profileDao;

    public UserController(UserDao userDao, ProfileDao profileDao) {
        this.userDao = userDao;
        this.profileDao = profileDao;
    }

    @DeleteMapping("/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        try {
            User user = userDao.getByUserName(username);
            if (user == null) {
                return new ResponseEntity<>("User not found.", HttpStatus.NOT_FOUND);
            }

            // 1. Profile sil
            profileDao.deleteByUserId(user.getId());

            // 2. Kullanıcı sil
            userDao.delete(username); // veya deleteByUsername(username);

            return new ResponseEntity<>("User deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Error deleting user.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        try {
            return new ResponseEntity<>(userDao.getAllUsers(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving users.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
