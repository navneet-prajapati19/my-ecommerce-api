package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.SignupRequest;
import com.example.ecommerce.service.AuthService;
import jakarta.validation.Valid;
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
    public ResponseEntity<CustomResponse<?>> signup(@Valid @RequestBody SignupRequest request) {
        try {
            CustomResponse<?> customResponse = authService.createUser(request);
            return ResponseEntity.status(customResponse.getStatus()).body(customResponse);
        } catch (Exception e) {
            CustomResponse<Object> errorResponse = new CustomResponse<>(500, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CustomResponse<?>> login(@Valid @RequestBody LoginRequest request) {
        try {
            CustomResponse<?> customResponse = authService.loginUser(request);
            return ResponseEntity.status(customResponse.getStatus()).body(customResponse);
        } catch (Exception e) {
            CustomResponse<Object> errorResponse = new CustomResponse<>(500, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
