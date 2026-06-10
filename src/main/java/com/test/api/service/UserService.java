package com.test.api.service;

import com.test.api.models.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    void saveUser(User User);
    void updateUser(User User);
    void deleteUser(Long id);

    List<User> getAllUsers();
}

