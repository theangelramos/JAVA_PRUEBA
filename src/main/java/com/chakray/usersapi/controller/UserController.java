package com.chakray.usersapi.controller;

import com.chakray.usersapi.model.User;
import com.chakray.usersapi.model.LoginRequest;
import com.chakray.usersapi.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers(
            @RequestParam(required = false) String sortedBy,
            @RequestParam(required = false) String filter) {

        if (filter != null && !filter.isEmpty()) {
            return userService.getUsersFiltered(filter);
        }
        if (sortedBy != null && !sortedBy.isEmpty()) {
            return userService.getUsersSorted(sortedBy);
        }
        return userService.getAllUsers();
    }

    // POST /users
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // PATCH /users/{id}
    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable UUID id,
            @RequestBody User user) {
        User updated = userService.updateUser(id, user);

        if (updated == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updated);
    }

    // DELETE /users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        User user = userService.authenticate(request.getTax_id(), request.getPassword());
        if (user != null) {
            return ResponseEntity.ok("Login successful");
        }
        return ResponseEntity.status(401).body("Invalid tax_id or password");
    }

    @GetMapping("/filter")
    public List<User> filterUsers(@RequestParam String filter) {
        return userService.getUsersFiltered(filter);
    }

}
