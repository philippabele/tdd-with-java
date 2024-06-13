package com.todoapp.controller;

import com.todoapp.model.Todo;
import com.todoapp.model.TodoRequest;
import com.todoapp.model.User;
import com.todoapp.repository.TodoRepository;
import com.todoapp.repository.UserRepository;
import com.todoapp.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;
    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    @Autowired
    public TodoController(TodoService todoService, TodoRepository todoRepository, UserRepository userRepository) {
        this.todoService = todoService;
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @PostMapping
    public ResponseEntity<Todo> addTodo(@Valid @RequestBody TodoRequest todoRequest) {
        Todo todo = new Todo();
        todo.setTitle(todoRequest.getTitle());
        todo.setDescription(todoRequest.getDescription());
        todo.setDueDate(todoRequest.getDueDate());
        todo.setCompleted(todoRequest.isCompleted());

        Todo savedTodo = todoService.save(todo);
        return new ResponseEntity<>(savedTodo, HttpStatus.CREATED);
    }

    /*
    @GetMapping
    public List<Todo> getTodos() {
        return todoService.findAll();
    }
     */

    @GetMapping
    public ResponseEntity<List<Todo>> getTodos(Authentication authentication) {
        // Authenticated user
        String username = authentication.getName();

        // Find user by username
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();

        // Find todos for the user
        List<Todo> todos = todoRepository.findByUserId(user.getId());

        return ResponseEntity.ok(todos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Optional<Todo> todo = todoService.findById(id);
        return todo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @Valid @RequestBody TodoRequest todoRequest) {
        Optional<Todo> existingTodo = todoService.findById(id);

        if (existingTodo.isPresent()) {
            Todo todo = existingTodo.get();
            todo.setTitle(todoRequest.getTitle());
            todo.setDescription(todoRequest.getDescription());
            todo.setDueDate(todoRequest.getDueDate());
            todo.setCompleted(todoRequest.isCompleted());
            Todo updatedTodo = todoService.save(todo);
            return ResponseEntity.ok(updatedTodo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodoById(@PathVariable Long id) {
        Optional<Todo> existingTodo = todoService.findById(id);
        if (existingTodo.isPresent()) {
            todoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
