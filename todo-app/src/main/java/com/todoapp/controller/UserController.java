package com.todoapp.controller;

import com.todoapp.model.User;
import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Endpoint for user registration
    @PostMapping("/api/register")
    public ResponseEntity<?> registerUser(@Validated @RequestBody UserRegistrationRequest request, BindingResult result) {
        // Check for validation results
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body("Invalid request");
        }
        // Check if the username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }
        // Check if password and confirmation match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Password and confirm password do not match");
        }

        try {
            // Create and save user with encrypted password
            User user = new User(request.getUsername(), passwordEncoder.encode(request.getPassword()));
            userRepository.save(user);
            // Successful response with the created user
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DataIntegrityViolationException e) {
            // Error saving user to the database
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving user to the database");
        }
    }
}
