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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private UserDetailsService userDetailsService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // This test method checks if user registration is successful
    @Test
    public void testUserRegistration_sucess() throws Exception {
        // Create a user registration request
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(new User("username", "encodedPassword"));

        // Perform a POST request to the /api/register endpoint with the user registration request as JSON content
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                // Verify that the response status is 201 (Created)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                // Verify that the response contains the expected token
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
    }

    // This test method checks if user registration fails when the username already exists
    @Test
    public void testUserRegistration_usernameAlreadyExists() throws Exception {
        // Create a user registration request with an existing username
        UserRegistrationRequest request = new UserRegistrationRequest("existingUsername", "password", "password");

        // Define the behavior of userRepository when checking if the username already exists
        when(userRepository.existsByUsername("existingUsername")).thenReturn(true);

        // Perform a POST request to the /api/register endpoint with the user registration request as JSON content
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                // Verify that the response status is 409 (Conflict) since the username already exists
                .andExpect(MockMvcResultMatchers.status().isConflict())
                // Verify that the body contains a specific error message
                .andExpect(MockMvcResultMatchers.content().string("Username already exists"));
    }

    // This test method verifies if user registration fails when the password confirmation does not match the password
    @Test
    public void testUserRegistration_passwordMismatch() throws Exception {
        // Create a user registration request where the password and confirmation do not match
        UserRegistrationRequest request = new UserRegistrationRequest("newuser", "password123", "password321");

        // No need to define the behavior of userRepository.existsByUsername since the validation should fail before reaching that check
        // Perform a POST request to the /api/register endpoint with the user registration request as JSON content
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                // Verify that the response status is 409 (Conflict) due to password mismatch
                .andExpect(MockMvcResultMatchers.status().isConflict())
                // Verify that the body contains a specific error message
                .andExpect(MockMvcResultMatchers.content().string("Password and confirm password do not match"));
    }

    @Test
    public void testUserRegistration_invalidInput() throws Exception {
        // Create a user registration request with invalid data
        UserRegistrationRequest request = new UserRegistrationRequest("", "", ""); // Leere Strings als ungültige Eingaben

        // Perform a POST request to the /api/register endpoint with the invalid user registration request
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                // Verify that the response status is 400 (Bad Request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUserRegistration_databaseError() throws Exception {
        // Create a user registration request
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        // Define the behavior of userRepository to simulate a database error
        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("Database error"));

        // Perform a POST request to the /api/register endpoint with the user registration request as JSON content
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                // Verify that the response status is 500 (Internal Server Error)
                .andExpect(MockMvcResultMatchers.status().isInternalServerError())
                // Verify that the response body contains the error message
                .andExpect(MockMvcResultMatchers.content().string("Error saving user to the database"));
    }

    @Test
    public void testUserRegistrationReturnsToken() throws Exception {
        // Cleanup before test
        userRepository.deleteAll();

        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"username\",\"password\":\"password\",\"confirmPassword\":\"password\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").isNotEmpty());
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
