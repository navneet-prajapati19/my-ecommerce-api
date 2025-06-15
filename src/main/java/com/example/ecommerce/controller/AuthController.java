package com.example.ecommerce.controller;

import com.example.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.ws.rs.core.Response;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public Response signup(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        return authService.createUser(username, password, email);
    }

    @PostMapping("/login")
    public Response login(@RequestParam String username, @RequestParam String password, @RequestParam String email) {
        return authService.loginUser(username, password);
    }
}
