package com.todoapp.service;

import com.todoapp.model.User;
import com.todoapp.model.UserLoginRequest;
import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class AuthenticationServiceIntegrationTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterUser_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", "password");

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword"); // Mock for PasswordEncoder
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            return user;
        });

        User registeredUser = authenticationService.register(request);

        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword()); // Vergleich mit der erwarteten codierten Zeichenfolge

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testAuthenticateUser_Success() {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest("testuser", "password", "password");
        User registeredUser = new User(registrationRequest.getUsername(), passwordEncoder.encode(registrationRequest.getPassword()));
        when(userService.saveUser(any(User.class))).thenReturn(registeredUser);

        UserLoginRequest loginRequest = new UserLoginRequest("testuser", "password");
        User user = new User(loginRequest.getUsername(), passwordEncoder.encode(loginRequest.getPassword()));
        when(userService.findUserByUsername(loginRequest.getUsername())).thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        User savedUser = authenticationService.register(registrationRequest);
        assertNotNull(savedUser);

        User authenticatedUser = authenticationService.authenticate(loginRequest);

        assertNotNull(authenticatedUser);
        assertEquals("testuser", authenticatedUser.getUsername());

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(userService).saveUser(any(User.class));
        verify(userService).findUserByUsername(loginRequest.getUsername());
    }


    @Test
    void testAuthenticateUser_UserNotFound() {
        UserLoginRequest request = new UserLoginRequest("unknownuser", "password");

        when(userService.findUserByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authenticationService.authenticate(request));

        verify(authenticationManager, never()).authenticate(any(Authentication.class));
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        // Mocking
        UserLoginRequest request = new UserLoginRequest("testuser", "wrongpassword");
        User user = new User(request.getUsername(), passwordEncoder.encode("password"));

        when(userService.findUserByUsername(request.getUsername())).thenReturn(Optional.of(user));
        when(authenticationManager.authenticate(any(Authentication.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(AuthenticationException.class, () -> authenticationService.authenticate(request));

        verify(authenticationManager).authenticate(any(Authentication.class));
        verify(userService).findUserByUsername(request.getUsername());
    }

}
