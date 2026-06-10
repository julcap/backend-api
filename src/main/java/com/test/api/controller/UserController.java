package com.test.api.controller;

import com.test.api.dto.MessageResponse;
import com.test.api.dto.UpdateUserRequest;
import com.test.api.models.User;
import com.test.api.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    public void addUser(@Valid @RequestBody UpdateUserRequest request) {
        User user = new User(request.getName(),request.getSurname(),"123" ,request.getBirthdate(),"Denmark","Address");
        UserService.saveUser(user);
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
