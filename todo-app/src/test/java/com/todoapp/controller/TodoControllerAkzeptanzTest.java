package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.model.TodoRequest;
import com.todoapp.model.UserLoginRequest;
import com.todoapp.model.UserRegistrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class TodoControllerAkzeptanzTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testAddTodo() throws Exception {
        // Mocking authenticated user (e.g., using MockMvc)
        UserRegistrationRequest userRegistrationRequest = new UserRegistrationRequest("testuser", "password", "password");
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegistrationRequest)))
                .andExpect(status().isOk());

        UserLoginRequest userLoginRequest = new UserLoginRequest("testuser", "password");
        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userLoginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String token = result.getResponse().getContentAsString();

        TodoRequest todoRequest = new TodoRequest("Test Todo", "This is a test todo", LocalDate.now(), false);
        mockMvc.perform(post("/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(todoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Test Todo"))
                .andExpect(jsonPath("$.description").value("This is a test todo"));
    }
}
