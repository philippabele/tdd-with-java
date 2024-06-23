package com.todoapp.controller;

import com.todoapp.model.Todo;
import com.todoapp.model.TodoRequest;
import com.todoapp.model.User;
import com.todoapp.service.TodoService;
import com.todoapp.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class TodoControllerTest {

    @Mock
    private TodoService todoService;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private TodoController todoController;

    private User testUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testUser");
        testUser.setPassword("testPassword");

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findUserByUsername(anyString())).thenReturn(Optional.of(testUser));
    }


    @Test
    public void testFindAllByUserId() {

        Todo todo1 = new Todo();
        todo1.setTitle("Todo 1");
        todo1.setDescription("Description 1");
        todo1.setDueDate(LocalDate.now());
        todo1.setCompleted(false);
        todo1.setUser(testUser);

        Todo todo2 = new Todo();
        todo2.setTitle("Todo 2");
        todo2.setDescription("Description 2");
        todo2.setDueDate(LocalDate.now().plusDays(1));
        todo2.setCompleted(true);
        todo2.setUser(testUser);

        todoService.addTodo(todo1);
        todoService.addTodo(todo2);

        List<Todo> todos = todoService.findAllByUserId(testUser.getId());

        assertEquals(2, todos.size());
        assertEquals("Todo 1", todos.get(0).getTitle());
        assertEquals("Todo 2", todos.get(1).getTitle());
    }

    @Test
    public void testAddTodo_Success() {
        TodoRequest todoRequest = new TodoRequest("Test Title", "Test Description", LocalDate.now(), false);
        Todo todo = new Todo("Test Title", "Test Description", LocalDate.now(), false, testUser);

        when(todoService.addTodo(any(Todo.class))).thenReturn(todo);

        ResponseEntity<Todo> response = todoController.addTodo(todoRequest, authentication);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(todo, response.getBody());
        verify(todoService, times(1)).addTodo(any(Todo.class));
    }

    @Test
    public void testAddTodo_UserNotFound() {
        TodoRequest todoRequest = new TodoRequest("Test Title", "Test Description", LocalDate.now(), false);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userService.findUserByUsername(anyString())).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            todoController.addTodo(todoRequest, authentication);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertEquals("404 NOT_FOUND \"User not found\"", exception.getMessage());

        verify(todoService, times(0)).addTodo(any(Todo.class));
    }

    @Test
    public void testGetTodos_Success() {
        User user = new User();
        user.setId(1L);
        user.setUsername("testUser");

        Todo todo1 = new Todo("Todo 1", "Description 1", LocalDate.now(), false, user);
        Todo todo2 = new Todo("Todo 2", "Description 2", LocalDate.now().plusDays(1), true, user);

        List<Todo> todos = Arrays.asList(todo1, todo2);
        when(authentication.getName()).thenReturn("testUser");
        when(userService.findUserByUsername(anyString())).thenReturn(Optional.of(user));
        when(todoService.findAllByUserId(anyLong())).thenReturn(todos);

        todos.forEach(todo -> todoService.addTodo(todo));
        ResponseEntity<List<Todo>> response = todoController.getTodos(authentication);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(todos.size(), response.getBody().size());
        assertEquals(todos.get(0).getTitle(), response.getBody().get(0).getTitle());
        assertEquals(todos.get(1).getTitle(), response.getBody().get(1).getTitle());

        verify(todoService, times(1)).findAllByUserId(user.getId());
        verify(userService, times(1)).findUserByUsername(user.getUsername());
    }

    @Test
    public void testGetTodos_UserNotFound() {
        when(userService.findUserByUsername(anyString())).thenReturn(Optional.empty());

        ResponseEntity<List<Todo>> response = todoController.getTodos(authentication);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(todoService, times(0)).findAllByUserId(anyLong());
    }

    @Test
    public void testGetTodoById_Success() {
        Todo todo = new Todo();
        when(todoService.findById(anyLong())).thenReturn(Optional.of(todo));

        ResponseEntity<Todo> response = todoController.getTodoById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(todo, response.getBody());
        verify(todoService, times(1)).findById(anyLong());
    }

    @Test
    public void testGetTodoById_NotFound() {
        when(todoService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Todo> response = todoController.getTodoById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(todoService, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdateTodo_Success() {
        TodoRequest todoRequest = new TodoRequest("Updated Title", "Updated Description", LocalDate.now(), true);
        Todo existingTodo = new Todo();
        when(todoService.findById(anyLong())).thenReturn(Optional.of(existingTodo));
        when(todoService.addTodo(any(Todo.class))).thenReturn(existingTodo);

        ResponseEntity<Todo> response = todoController.updateTodo(1L, todoRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(todoService, times(1)).findById(anyLong());
        verify(todoService, times(1)).addTodo(any(Todo.class));
    }

    @Test
    public void testUpdateTodo_NotFound() {
        TodoRequest todoRequest = new TodoRequest("Updated Title", "Updated Description", LocalDate.now(), true);
        when(todoService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Todo> response = todoController.updateTodo(1L, todoRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(todoService, times(1)).findById(anyLong());
        verify(todoService, times(0)).addTodo(any(Todo.class));
    }

    @Test
    public void testDeleteTodoById_Success() {
        Todo existingTodo = new Todo();
        when(todoService.findById(anyLong())).thenReturn(Optional.of(existingTodo));

        ResponseEntity<Void> response = todoController.deleteTodoById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(todoService, times(1)).findById(anyLong());
        verify(todoService, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteTodoById_NotFound() {
        when(todoService.findById(anyLong())).thenReturn(Optional.empty());

        ResponseEntity<Void> response = todoController.deleteTodoById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(todoService, times(1)).findById(anyLong());
        verify(todoService, times(0)).deleteById(anyLong());
    }
}
