package com.todoapp.service;

import com.todoapp.model.User;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveUser() {
        // Mock data
        String username = "testuser";
        String plainPassword = "testpassword";
        String encodedPassword = "encodedPassword"; // Mock encoded password
        User mockUser = new User(username, encodedPassword);

        // Mock behavior of userRepository.save()
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // Mock behavior of passwordEncoder.encode()
        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);

        // Call the service method
        User savedUser = userService.saveUser(mockUser);

        // Assertions
        assertNotNull(savedUser);
        assertEquals(username, savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
    }

    @Test
    public void testFindUserByUsername() {
        // Mock data
        String username = "testuser";
        User mockUser = new User(username, "encodedPassword");

        // Mock behavior of userRepository.findByUsername()
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        // Call the service method
        Optional<User> foundUserOptional = userService.findUserByUsername(username);

        // Assertions
        assertTrue(foundUserOptional.isPresent());
        assertEquals(username, foundUserOptional.get().getUsername());
    }

    @Test
    public void testSaveUser_UsernameAlreadyExists() {
        // Mock data
        String existingUsername = "existinguser";
        User existingUser = new User(existingUsername, "existingEncodedPassword");

        // Mock behavior of userRepository.existsByUsername()
        when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

        // Create a new user with existing username
        User newUser = new User(existingUsername, "newEncodedPassword");

        // Mock behavior of passwordEncoder.encode()
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn(newUser.getPassword());

        // Mock behavior of userRepository.save()
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Username already exists"));

        // Call the service method and assert exception
        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUser(newUser));

        // Assertion
        assertEquals("Username already exists", exception.getMessage());
    }


    @Test
    public void testFindUserByUsername_NotFound() {
        // Mock data
        String nonExistentUsername = "nonexistentuser";

        // Mock behavior of userRepository.findByUsername()
        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        // Call the service method
        Optional<User> foundUserOptional = userService.findUserByUsername(nonExistentUsername);

        // Assertions
        assertTrue(foundUserOptional.isEmpty());
    }
}
