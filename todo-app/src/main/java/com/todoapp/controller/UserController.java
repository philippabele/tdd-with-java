package com.todoapp.controller;

import com.todoapp.model.LoginResponse;
import com.todoapp.model.User;
import com.todoapp.model.UserLoginRequest;
import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.service.AuthenticationService;
import com.todoapp.service.JwtService;
import com.todoapp.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationService authenticationService;
/*
    public UserController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

 */

    // Endpoint for user registration
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request, BindingResult result) {

        // Check if the username already exists
        if (userService.existsByUsername(request.getUsername())) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
        }

        // Check if password and confirmation match
        if (!request.getPassword().equals(request.getConfirmPassword())) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body("Password and confirm password do not match");
        }

        // Check for validation results
        if (result.hasErrors()) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation error: " + result.getAllErrors());
        }

        try {
            User registeredUser = authenticationService.register(request);

            String token = jwtService.generateToken(registeredUser);

            return ResponseEntity.ok(token);
            //return ResponseEntity.ok(registeredUser);
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid UserLoginRequest request) {

        try {
            User authenticatedUser = authenticationService.authenticate(request);
            if (authenticatedUser == null || !passwordEncoder.matches(request.getPassword(), authenticatedUser.getPassword())) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }

            String jwtToken = jwtService.generateToken(authenticatedUser);
            LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());

            return ResponseEntity.ok(loginResponse);
        } catch (RuntimeException e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Server Error");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return ResponseEntity.ok("Logout erfolgreich");
    }
}
