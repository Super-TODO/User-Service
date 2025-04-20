package com.spring.userservice.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {
    @GetMapping("/secure")
    public ResponseEntity<String> secureTest() {
        return ResponseEntity.ok(" Welcome! You are authenticated and allowed to access this.");
    }

    @GetMapping("/public")
    public ResponseEntity<String> publicTest() {
        return ResponseEntity.ok(" This is a public endpoint. No token needed.");
    }
}
