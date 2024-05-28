package com.todoapp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.todoapp.config.JacksonConfig;
import com.todoapp.config.SecurityConfig;
import com.todoapp.model.Todo;
import com.todoapp.model.TodoRequest;
import com.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(TodoController.class)
@Import({SecurityConfig.class, JacksonConfig.class})
@ActiveProfiles("test")
public class TodoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @WithMockUser(username = "user")
    public void testAddTodo_success() throws Exception {
        TodoRequest request = new TodoRequest("Test Title", "Test Description", LocalDate.of(2024, 5, 30), false);

        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Title");
        todo.setDescription("Test Description");
        todo.setDueDate(LocalDate.of(2024, 5, 30));
        todo.setCompleted(false);

        given(todoRepository.save(any(Todo.class))).willReturn(todo);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.description").value("Test Description"))
                .andExpect(jsonPath("$.dueDate[0]").value(2024))
                .andExpect(jsonPath("$.dueDate[1]").value(5))
                .andExpect(jsonPath("$.dueDate[2]").value(30))
                .andExpect(jsonPath("$.completed").value(false));
    }


    @Test
    @WithMockUser(username = "user")
    public void testGetTodos_success() throws Exception {
        Todo todo1 = new Todo();
        todo1.setId(1L);
        todo1.setTitle("Test Title 1");
        todo1.setDescription("Test Description 1");
        todo1.setDueDate(LocalDate.of(2024, 5, 30));
        todo1.setCompleted(false);

        Todo todo2 = new Todo();
        todo2.setId(2L);
        todo2.setTitle("Test Title 2");
        todo2.setDescription("Test Description 2");
        todo2.setDueDate(LocalDate.of(2024, 6, 15));
        todo2.setCompleted(true);

        List<Todo> allTodos = Arrays.asList(todo1, todo2);

        given(todoRepository.findAll()).willReturn(allTodos);

        mockMvc.perform(get("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Title 1"))
                .andExpect(jsonPath("$[0].description").value("Test Description 1"))
                .andExpect(jsonPath("$[0].dueDate[0]").value(2024))
                .andExpect(jsonPath("$[0].dueDate[1]").value(5))
                .andExpect(jsonPath("$[0].dueDate[2]").value(30))
                .andExpect(jsonPath("$[0].completed").value(false))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].title").value("Test Title 2"))
                .andExpect(jsonPath("$[1].description").value("Test Description 2"))
                .andExpect(jsonPath("$[1].dueDate[0]").value(2024))
                .andExpect(jsonPath("$[1].dueDate[1]").value(6))
                .andExpect(jsonPath("$[1].dueDate[2]").value(15))
                .andExpect(jsonPath("$[1].completed").value(true));
    }

    private String asJsonString(final Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
