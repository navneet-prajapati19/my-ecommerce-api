package com.example.ecommerce.errorHandler;

import com.example.ecommerce.dto.ErrorResponse;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;

public class ErrorResponseDeserializer extends JsonDeserializer<ErrorResponse> {

    @Override
    public ErrorResponse deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // Check all possible field names
        String errorMessage = null;
        if (node.has("errorMessage")) {
            errorMessage = node.get("errorMessage").asText();
        } else if (node.has("error_description")) {
            errorMessage = node.get("error_description").asText();
        } else if (node.has("error")) {
            errorMessage = node.get("error").asText();
        }

        return new ErrorResponse(errorMessage);
    }
}
