package com.example.ecommerce.dto;

import com.example.ecommerce.errorHandler.ErrorResponseDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = ErrorResponseDeserializer.class)
public record ErrorResponse(String errorMessage) {
}
