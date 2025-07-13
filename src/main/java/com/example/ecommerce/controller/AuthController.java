package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomResponse;
import com.example.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<CustomResponse<?>> signup(@RequestParam String email, @RequestParam String password, @RequestParam String firstName, @RequestParam String lastName) {
        try {
            CustomResponse<?> customResponse = authService.createUser(email, password, firstName, lastName);
            return ResponseEntity.ok(customResponse);
        } catch (Exception e) {
            CustomResponse<Object> errorResponse = new CustomResponse<>(500, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<?>> login(@RequestParam String email, @RequestParam String password) {
        try {
            CustomResponse<?> customResponse = authService.loginUser(email, password);
            return ResponseEntity.ok(customResponse);
        } catch (Exception e) {
            CustomResponse<Object> errorResponse = new CustomResponse<>(500, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
