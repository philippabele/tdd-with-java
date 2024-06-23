package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todoapp.model.Todo;
import com.todoapp.model.TodoRequest;
import com.todoapp.model.User;
import com.todoapp.service.TodoService;
import com.todoapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TodoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TodoService todoService;

    @MockBean
    private UserService userService;

    private User testUser;
    private Todo testTodo;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "password");
        testUser.setId(1L);
        testTodo = new Todo("Test Todo", "Test Description", LocalDate.now(), false, testUser);
        testTodo.setId(1L);
    }

    @Test
    @WithMockUser(username = "testuser")
    void addTodo_ValidInput_ReturnsCreated() throws Exception {
        TodoRequest todoRequest = new TodoRequest("Test Todo", "Test Description", LocalDate.now(), false);
        String jsonRequest = objectMapper.writeValueAsString(todoRequest);

        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(todoService.addTodo(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testTodo.getId().intValue()))
                .andExpect(jsonPath("$.title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$.description").value(testTodo.getDescription()));
    }

    @Test
    @WithMockUser(username = "nonexistentuser")
    void addTodo_UserNotFound_ReturnsNotFound() throws Exception {
        TodoRequest todoRequest = new TodoRequest("Test Todo", "Test Description", LocalDate.now(), false);
        String jsonRequest = objectMapper.writeValueAsString(todoRequest);

        when(userService.findUserByUsername("nonexistentuser")).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTodos_ReturnsListOfTodos() throws Exception {
        when(userService.findUserByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(todoService.findAllByUserId(1L)).thenReturn(Arrays.asList(testTodo));

        mockMvc.perform(MockMvcRequestBuilders.get("/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testTodo.getId().intValue()))
                .andExpect(jsonPath("$[0].title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$[0].description").value(testTodo.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTodoById_ExistingId_ReturnsTodo() throws Exception {
        when(todoService.findById(1L)).thenReturn(Optional.of(testTodo));

        mockMvc.perform(MockMvcRequestBuilders.get("/todos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId().intValue()))
                .andExpect(jsonPath("$.title").value(testTodo.getTitle()))
                .andExpect(jsonPath("$.description").value(testTodo.getDescription()));
    }

    @Test
    @WithMockUser(username = "testuser")
    void getTodoById_NonExistingId_ReturnsNotFound() throws Exception {
        when(todoService.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.get("/todos/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateTodo_ExistingId_ReturnsUpdatedTodo() throws Exception {
        TodoRequest todoRequest = new TodoRequest("Updated Todo", "Updated Description", LocalDate.now(), true);
        String jsonRequest = objectMapper.writeValueAsString(todoRequest);

        when(todoService.findById(1L)).thenReturn(Optional.of(testTodo));
        when(todoService.addTodo(any(Todo.class))).thenReturn(testTodo);

        mockMvc.perform(MockMvcRequestBuilders.put("/todos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTodo.getId().intValue()))
                .andExpect(jsonPath("$.title").value(todoRequest.getTitle()))
                .andExpect(jsonPath("$.description").value(todoRequest.getDescription()))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @WithMockUser(username = "testuser")
    void updateTodo_NonExistingId_ReturnsNotFound() throws Exception {
        TodoRequest todoRequest = new TodoRequest("Updated Todo", "Updated Description", LocalDate.now(), true);
        String jsonRequest = objectMapper.writeValueAsString(todoRequest);

        when(todoService.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.put("/todos/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTodoById_ExistingId_ReturnsNoContent() throws Exception {
        when(todoService.findById(1L)).thenReturn(Optional.of(testTodo));

        mockMvc.perform(MockMvcRequestBuilders.delete("/todos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "testuser")
    void deleteTodoById_NonExistingId_ReturnsNotFound() throws Exception {
        when(todoService.findById(2L)).thenReturn(Optional.empty());

        mockMvc.perform(MockMvcRequestBuilders.delete("/todos/2"))
                .andExpect(status().isNotFound());
    }
}