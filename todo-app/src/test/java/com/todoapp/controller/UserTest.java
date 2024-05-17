package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.config.JwtUtil;
import com.todoapp.config.SecurityConfig;
import com.todoapp.model.User;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, JwtUtil.class})
public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    // Tests successful user registration and token generation
    @Test
    public void testUserRegistration_sucess() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        given(userRepository.existsByUsername("username")).willReturn(false);
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(new User("username", "encodedPassword"));
        given(jwtUtil.generateToken("username")).willReturn("generatedToken");


        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
    }

    // Tests user registration failure when username already exists
    @Test
    public void testUserRegistration_usernameAlreadyExists() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("existingUsername", "password", "password");

        given(userRepository.existsByUsername("existingUsername")).willReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Username already exists"));
    }

    // Tests user registration failure due to password mismatch
    @Test
    public void testUserRegistration_passwordMismatch() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("newuser", "password123", "password321");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isConflict())
                .andExpect(MockMvcResultMatchers.content().string("Password and confirm password do not match"));
    }

    // Tests user registration with invalid input
    @Test
    public void testUserRegistration_invalidInput() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("", "", ""); // Leere Strings als ungültige Eingaben

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    // Tests database error during user registration
    @Test
    public void testUserRegistration_databaseError() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        given(userRepository.existsByUsername("username")).willReturn(false);
        given(userRepository.save(any(User.class))).willThrow(new DataIntegrityViolationException("Database error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Error saving user to the database"));
    }

    @Test
    public void testUserRegistrationReturnsToken() throws Exception {
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        given(userRepository.existsByUsername("username")).willReturn(false);
        given(passwordEncoder.encode("password")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(new User("username", "encodedPassword"));
        given(jwtUtil.generateToken("username")).willReturn("generatedToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("generatedToken"));
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
