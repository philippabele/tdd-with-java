package com.todoapp.controller;

import com.todoapp.model.LoginResponse;
import com.todoapp.model.User;
import com.todoapp.model.UserLoginRequest;
import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.service.AuthenticationService;
import com.todoapp.service.JwtService;
import com.todoapp.service.UserService;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationService authenticationService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserController userController;


    @Test
    public void testRegisterUserSuccess() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", "password");

        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(authenticationService.register(request)).thenReturn(new User("testuser", "encodedPassword"));

        BindingResult result = mock(BindingResult.class);
        ResponseEntity<?> response = userController.registerUser(request, result);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response);
        assertNotNull(response.getBody());
        assertEquals("testuser", ((User) response.getBody()).getUsername());
    }

    @Test
    public void testRegisterUserUsernameConflict() {
        UserRegistrationRequest request = new UserRegistrationRequest("existinguser", "password", "password");

        BindingResult result = mock(BindingResult.class);
        when(userService.existsByUsername("existinguser")).thenReturn(true);

        ResponseEntity<?> response = userController.registerUser(request, result);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }

    @Test
    public void testRegisterUserPasswordMismatch() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password1", "password2");

        ResponseEntity<?> response = userController.registerUser(request, null);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Password and confirm password do not match", response.getBody());
    }

    @Test
    public void testRegisterUserException() {
        UserRegistrationRequest request = new UserRegistrationRequest("testuser", "password", "password");

        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(authenticationService.register(request)).thenThrow(ConstraintViolationException.class);

        BindingResult result = mock(BindingResult.class);
        ResponseEntity<?> response = userController.registerUser(request, result);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error registering user", response.getBody());
    }

    @Test
    public void testLoginUserSuccess() {
        UserLoginRequest request = new UserLoginRequest("testuser", "password");

        User mockUser = new User("testuser", "encodedPassword");
        when(authenticationService.authenticate(request)).thenReturn(mockUser);
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtService.generateToken(mockUser)).thenReturn("mockJwtToken");

        ResponseEntity<?> response = userController.loginUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof LoginResponse);
        assertEquals("mockJwtToken", ((LoginResponse) response.getBody()).getToken());
    }

    @Test
    public void testLoginUserInvalidCredentials() {
        UserLoginRequest request = new UserLoginRequest("testuser", "wrongPassword");

        when(authenticationService.authenticate(request)).thenReturn(null);

        ResponseEntity<?> response = userController.loginUser(request);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }

    @Test
    public void testLoginUserException() {
        when(authenticationService.authenticate(any(UserLoginRequest.class))).thenThrow(RuntimeException.class);

        UserLoginRequest request = new UserLoginRequest("testuser", "password");
        ResponseEntity<?> response = userController.loginUser(request);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody());
    }
}
