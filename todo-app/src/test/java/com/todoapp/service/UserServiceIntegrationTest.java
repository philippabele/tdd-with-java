package com.todoapp.service;

import com.todoapp.model.User;
import com.todoapp.repository.UserRepository;
import com.todoapp.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = Replace.NONE)
@SpringBootTest
@Transactional
public class UserServiceIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setup() {

    }

    @AfterEach
    public void cleanup() {
        userRepository.deleteAll();
    }

    @Test
    public void testSaveUser() {
        String username = "testuser";
        String plainPassword = "testpassword";
        User user = new User(username, plainPassword);

        User savedUser = userService.saveUser(user);

        assertNotNull(savedUser.getId());
        assertEquals(username, savedUser.getUsername());

        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(username, retrievedUser.get().getUsername());
    }

    @Test
    public void testSaveUser_UsernameAlreadyExists() {
        String existingUsername = "existinguser";
        userRepository.save(new User(existingUsername, passwordEncoder.encode("password")));

        User newUser = new User(existingUsername, passwordEncoder.encode("newpassword"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> userService.saveUser(newUser));
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    public void testFindUserByUsername() {
        String username = "testuser";
        User user = new User(username, passwordEncoder.encode("password"));
        userRepository.save(user);

        User foundUser = userService.findUserByUsername(username).orElse(null);

        assertEquals(username, foundUser.getUsername());
    }

    @Test
    public void testFindUserByUsername_NotFound() {
        User foundUser = userService.findUserByUsername("nonexistentuser").orElse(null);

        assertEquals(null, foundUser);
    }
}
