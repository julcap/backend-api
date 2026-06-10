package com.backend.api.service;

import com.backend.api.models.User;
import com.backend.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository UserRepository;

    @Autowired
    public UserServiceImpl(UserRepository UserRepository) {
        this.UserRepository = UserRepository;
    }

    @Override
    public User getUserById(Long id) {
        Optional<User> optionalUser = UserRepository.findById(id);
        return optionalUser.orElse(null);
    }

    @Override
    public void saveUser(User User) {
        UserRepository.save(User);
    }

    @Override
    public void updateUser(User User) {
        UserRepository.save(User);
    }

    @Override
    public void deleteUser(Long id) {
        UserRepository.deleteById(id);
    }

    // Additional method to get all Users (optional)
    public List<User> getAllUsers() {
        return UserRepository.findAll();
    }
}
