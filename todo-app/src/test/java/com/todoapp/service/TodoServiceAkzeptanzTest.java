package com.todoapp.service;


import com.todoapp.model.Todo;
import com.todoapp.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class TodoServiceAkzeptanzTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private UserService userService;

    @Test
    void testAddTodo() {
        User user = new User("testuser", "password");
        userService.saveUser(user);

        Todo todo = new Todo("Test Todo", "This is a test todo", LocalDate.now(), false, user);
        Todo savedTodo = todoService.addTodo(todo);

        assertNotNull(savedTodo.getId());
        assertEquals("Test Todo", savedTodo.getTitle());
        assertEquals("This is a test todo", savedTodo.getDescription());
    }
}
