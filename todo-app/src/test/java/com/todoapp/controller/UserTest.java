package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.config.SecurityConfig;
import com.todoapp.model.User;
import com.todoapp.model.UserRegistrationRequest;
import com.todoapp.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)

public class UserTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    // This test method checks if user registration is successful
    @Test
    public void testUserRegistration_sucess() throws Exception {
        // Create a user registration request
        UserRegistrationRequest request = new UserRegistrationRequest("username", "password", "password");

        // Define the behavior of userRepository when saving a user
        when(userRepository.existsByUsername("username")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(new User("username", "password"));

        // Perform a POST request to the /api/register endpoint with the user registration request as JSON content
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                        // Verify that the response status is 201 (Created)
                        .andExpect(MockMvcResultMatchers.status().isCreated())
                        // Verify that the response contains the expected username
                        .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("username"));
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
