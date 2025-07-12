package com.example.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomResponse<T> {
    private int status;
    private String message;
    private T data;
}
