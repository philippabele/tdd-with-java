package com.todoapp.model;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class TodoTest {

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testTodoConstructorAndGettersSetters() {
        // Given
        User user = new User("testuser", "password");
        Todo todo = new Todo("Title", "Description", LocalDate.now(), false, user);

        todo.setId(1L);

        assertEquals("Title", todo.getTitle());
        assertEquals("Description", todo.getDescription());
        assertEquals(LocalDate.now(), todo.getDueDate());
        assertFalse(todo.isCompleted());
        assertEquals(user, todo.getUser());
        assertEquals(1L, todo.getId());
    }

}
