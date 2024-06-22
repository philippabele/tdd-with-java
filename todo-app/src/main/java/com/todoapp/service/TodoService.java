package com.todoapp.service;

import com.todoapp.model.Todo;
import com.todoapp.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {

    @Autowired
    private TodoRepository todoRepository;

    public Todo addTodo(Todo todo) {
        Todo addedTodo = todoRepository.save(todo);
        System.out.println("Added Todo: " + addedTodo); // Add this line for debugging
        return addedTodo;
        // return todoRepository.save(todo);
    }

    public List<Todo> findAll() {
        return todoRepository.findAll();
    }

    public List<Todo> findAllByUserId(Long userId) {
        return todoRepository.findByUserId(userId);
    }

    public Optional<Todo> findById(Long id) {
        return todoRepository.findById(id);
    }

    public void deleteById(Long id) {
        todoRepository.deleteById(id);
    }
}
