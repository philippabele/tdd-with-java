package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.config.JwtUtil;
import com.todoapp.config.SecurityConfig;
import com.todoapp.model.User;
import com.todoapp.model.UserLoginRequest;
import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtUtil.class})
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    // Tests successful user registration and token generation
    @Test
    @WithMockUser(username = "user")
    public void testUserRegistration_sucess() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        given(userRepository.existsByUsername("username")).willReturn(false);
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(new User("username", "encodedPassword"));
        given(jwtUtil.generateToken("username")).willReturn("generatedToken");


        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.token").isString());
    }

    // Tests user registration failure when username already exists
    @Test
    @WithMockUser(username = "user")
    public void testUserRegistration_usernameAlreadyExists() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("existingUsername", "password", "password");

        given(userRepository.existsByUsername("existingUsername")).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Username already exists"));
    }

    // Tests user registration failure due to password mismatch
    @Test
    @WithMockUser(username = "user")
    public void testUserRegistration_passwordMismatch() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("newuser", "password123", "password321");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Password and confirm password do not match"));
    }

    // Tests user registration with invalid input
    @Test
    @WithMockUser(username = "user")
    public void testUserRegistration_invalidInput() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("", "", ""); // Leere Strings als ungültige Eingaben

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isBadRequest());
    }

    // Tests database error during user registration
    @Test
    @WithMockUser(username = "user")
    public void testUserRegistration_databaseError() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        given(userRepository.existsByUsername("username")).willReturn(false);
        given(userRepository.save(any(User.class))).willThrow(new DataIntegrityViolationException("Database error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error saving user to the database"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testUserRegistrationReturnsToken() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        given(userRepository.existsByUsername("username")).willReturn(false);
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(new User("username", "encodedPassword"));
        given(jwtUtil.generateToken("username")).willReturn("generatedToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("generatedToken"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testUserLogin_success() throws Exception {
        UserLoginRequest request = new UserLoginRequest("username", "password");

        User user = new User("username", "encodedPassword");
        given(userRepository.findByUsername("username")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password", user.getPassword())).willReturn(true);
        given(jwtUtil.generateToken("username")).willReturn("generatedToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("generatedToken"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testUserLogin_invalidPassword() throws Exception {
        UserLoginRequest request = new UserLoginRequest("username", "wrongPassword");

        User user = new User("username", "encodedPassword");
        given(userRepository.findByUsername("username")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongPassword", user.getPassword())).willReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Invalid username or password"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testUserLogin_invalidUsername() throws Exception {
        UserLoginRequest request = new UserLoginRequest("username", "password");

        given(userRepository.findByUsername("username")).willReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Invalid username or password"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testUserLogin_userNotFound() throws Exception {
        UserLoginRequest request = new UserLoginRequest("username", "password");

        given(userRepository.findByUsername("username")).willReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$").value("Invalid username or password"));
    }

    @Test
    @WithMockUser(username = "user")
    public void testUserLogout_success() throws Exception {
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/logout"))
                .andExpect(status().isOk());
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }


    // Helper method to convert objects to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
