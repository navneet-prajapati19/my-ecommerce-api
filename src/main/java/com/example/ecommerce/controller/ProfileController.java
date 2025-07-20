package com.example.ecommerce.controller;

import com.example.ecommerce.dto.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
public class ProfileController {

    @PostMapping("/profile")
    public ResponseEntity<CustomResponse<?>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        String userId = jwt.getSubject();
        CustomResponse<Object> customResponse = new CustomResponse<>(200, userId, null);
        return ResponseEntity.status(customResponse.getStatus()).body(customResponse);
    }
}
