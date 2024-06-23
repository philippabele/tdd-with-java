package com.todoapp.controller;

import com.todoapp.model.Todo;
import com.todoapp.model.TodoRequest;
import com.todoapp.model.User;
import com.todoapp.service.TodoService;
import com.todoapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;
    private final UserService userService;

    @Autowired
    public TodoController(TodoService todoService, UserService userService) {
        this.todoService = todoService;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Todo> addTodo(@Valid @RequestBody TodoRequest todoRequest, Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.findUserByUsername(userDetails.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        Todo todo = new Todo(todoRequest.getTitle(),
                todoRequest.getDescription(),
                todoRequest.getDueDate(),
                todoRequest.isCompleted(),
                user);

        Todo savedTodo = todoService.addTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedTodo);
    }


    @GetMapping
    public ResponseEntity<List<Todo>> getTodos(Authentication authentication) {
        // Authenticated user
        String username = authentication.getName();

        // Find user by username
        Optional<User> optionalUser = userService.findUserByUsername(username);
        if (!optionalUser.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();

        // Find todos for the user
        List<Todo> todos = todoService.findAllByUserId(user.getId());

        // return ResponseEntity.ok(todos);
        return ResponseEntity.ok().body(todos);
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
            Todo updatedTodo = todoService.addTodo(todo);
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
