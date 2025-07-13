package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest (@NotBlank String email,
                             @NotBlank String password) {}
