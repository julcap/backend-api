package com.backend.api.controller;

import com.backend.api.dto.MessageResponse;
import com.backend.api.dto.UpdateUserRequest;
import com.backend.api.models.User;
import com.backend.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService UserService;

    @Autowired
    public UserController(UserService UserService) {
        this.UserService = UserService;
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return UserService.getUserById(id);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return UserService.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<User> addUser(@Valid @RequestBody UpdateUserRequest request) {
        User user = new User(
                request.getName(),
                request.getSurname(),
                request.getPhone(),
                request.getBirthdate(),
                request.getCountry(),
                request.getAddress());

        User savedUser = UserService.saveUser(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedUser);
    }

    @PutMapping("/{id}")
    public MessageResponse updateUser(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        User existingUser = UserService.getUserById(id);
        if (existingUser == null) {
            return new MessageResponse("User not found");
        }
        existingUser.setName(request.getName());
        existingUser.setSurname(request.getSurname());
        existingUser.setBirthdate(request.getBirthdate());
        UserService.saveUser(existingUser);
        return null;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        UserService.deleteUser(id);
    }
}
