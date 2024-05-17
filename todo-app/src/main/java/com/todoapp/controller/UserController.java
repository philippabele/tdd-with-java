package com.todoapp.controller;

import com.todoapp.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    @GetMapping("/getUser")
    public ResponseEntity<User> getUser(@RequestParam int id) {
        return ResponseEntity.ok().body(null);
    }
}
