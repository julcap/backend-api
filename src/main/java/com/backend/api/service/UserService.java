package com.backend.api.service;

import com.backend.api.models.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    User saveUser(User User);
    void updateUser(User User);
    void deleteUser(Long id);

    List<User> getAllUsers();
}

