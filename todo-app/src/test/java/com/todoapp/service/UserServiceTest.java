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
        String username = "testuser";
        String plainPassword = "testpassword";
        String encodedPassword = "encodedPassword"; // Mock encoded password
        User mockUser = new User(username, encodedPassword);

        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        when(passwordEncoder.encode(plainPassword)).thenReturn(encodedPassword);

        User savedUser = userService.saveUser(mockUser);

        assertNotNull(savedUser);
        assertEquals(username, savedUser.getUsername());
        assertEquals(encodedPassword, savedUser.getPassword());
    }

    @Test
    public void testFindUserByUsername() {
        String username = "testuser";
        User mockUser = new User(username, "encodedPassword");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));

        Optional<User> foundUserOptional = userService.findUserByUsername(username);

        assertTrue(foundUserOptional.isPresent());
        assertEquals(username, foundUserOptional.get().getUsername());
    }

    @Test
    public void testSaveUser_UsernameAlreadyExists() {
        String existingUsername = "existinguser";
        User existingUser = new User("existinguser", "existingEncodedPassword");

        when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

        User newUser = new User(existingUsername, "newEncodedPassword");

        when(passwordEncoder.encode(newUser.getPassword())).thenReturn(newUser.getPassword());

        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Username already exists"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUser(newUser));

        assertEquals("Username already exists", exception.getMessage());
    }


    @Test
    public void testFindUserByUsername_NotFound() {
        String nonExistentUsername = "nonexistentuser";

        when(userRepository.findByUsername(nonExistentUsername)).thenReturn(Optional.empty());

        Optional<User> foundUserOptional = userService.findUserByUsername(nonExistentUsername);

        assertTrue(foundUserOptional.isEmpty());
    }
}
