package com.todoapp.service;

import com.todoapp.model.User;
import com.todoapp.model.UserLoginRequest;
import com.todoapp.model.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class AuthenticationServiceTest {

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthenticationService authenticationService;


    @Test
    void testRegisterUser_Success() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", "password");
        User user = new User(request.getUsername(), "encodedPassword");

        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.saveUser(any(User.class))).thenReturn(user);

        User registeredUser = authenticationService.register(request);

        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userService).saveUser(userCaptor.capture());
        assertEquals("encodedPassword", userCaptor.getValue().getPassword());
    }

    @Test
    void testAuthenticateUser_Success() {
        UserLoginRequest request = new UserLoginRequest("testuser", "password");
        User user = new User("testuser", "encodedPassword");

        when(userService.findUserByUsername(request.getUsername())).thenReturn(Optional.of(user));
        doNothing().when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        User authenticatedUser = authenticationService.authenticate(request);

        assertNotNull(authenticatedUser);
        assertEquals("testuser", authenticatedUser.getUsername());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticateUser_UserNotFound() {
        UserLoginRequest request = new UserLoginRequest("unknownuser", "password");

        when(userService.findUserByUsername(request.getUsername())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            authenticationService.authenticate(request);
        });

        verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    void testAuthenticateUser_InvalidPassword() {
        UserLoginRequest request = new UserLoginRequest("testuser", "wrongpassword");

        User user = new User("testuser", "encodedPassword");

        when(userService.findUserByUsername(request.getUsername())).thenReturn(Optional.of(user));
        doThrow(new AuthenticationException("Invalid credentials") {
        }).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThrows(AuthenticationException.class, () -> {
            authenticationService.authenticate(request);
        });
    }

    /*
    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    public void testRegisterAndAuthenticateUser() {
        UserRegistrationRequest registrationRequest = new UserRegistrationRequest("testuser", "password", "password");
        User registeredUser = authenticationService.register(registrationRequest);

        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        assertTrue(passwordEncoder.matches("password", registeredUser.getPassword()));

        UserLoginRequest loginRequest = new UserLoginRequest("testuser", "password");
        User authenticatedUser = authenticationService.authenticate(loginRequest);

        assertNotNull(authenticatedUser);
        assertEquals("testuser", authenticatedUser.getUsername());
    }

     */
}
