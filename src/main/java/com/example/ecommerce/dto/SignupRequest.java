package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest (@NotBlank String email,
                             @NotBlank String password,
                             @NotBlank String firstName,
                             @NotBlank String lastName) {}
