package com.todoapp.service;

import com.todoapp.model.Todo;
import com.todoapp.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@SpringJUnitConfig(TodoService.class)
public class TodoServiceTest {

    @MockBean
    private TodoRepository todoRepository;

    @Autowired
    private TodoService todoService;

    private Todo todo;

    @BeforeEach
    public void setup() {
        todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Title");
        todo.setDescription("Test Description");
        todo.setDueDate(LocalDate.of(2024, 5, 30));
        todo.setCompleted(false);
    }

    @Test
    public void testSaveTodo() {
        given(todoRepository.save(any(Todo.class))).willReturn(todo);
        Todo savedTodo = todoService.save(todo);
        assertEquals(todo, savedTodo);
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    public void testFindAllTodos() {
        List<Todo> todos = Arrays.asList(todo);
        given(todoRepository.findAll()).willReturn(todos);
        List<Todo> foundTodos = todoService.findAll();
        assertEquals(todos, foundTodos);
        verify(todoRepository, times(1)).findAll();
    }

    @Test
    public void testFindTodoById() {
        given(todoRepository.findById(1L)).willReturn(Optional.of(todo));
        Optional<Todo> foundTodo = todoService.findById(1L);
        assertTrue(foundTodo.isPresent());
        assertEquals(todo, foundTodo.get());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindTodoByIdNotFound() {
        given(todoRepository.findById(1L)).willReturn(Optional.empty());
        Optional<Todo> foundTodo = todoService.findById(1L);
        assertTrue(foundTodo.isEmpty());
        verify(todoRepository, times(1)).findById(1L);
    }

    @Test
    public void testUpdateTodo() {
        given(todoRepository.save(any(Todo.class))).willReturn(todo);
        todo.setTitle("Updated Title");
        Todo updatedTodo = todoService.save(todo);
        assertEquals("Updated Title", updatedTodo.getTitle());
        verify(todoRepository, times(1)).save(todo);
    }

    @Test
    public void testDeleteTodoById() {
        todoService.deleteById(1L);
        verify(todoRepository, times(1)).deleteById(1L);
    }
}
