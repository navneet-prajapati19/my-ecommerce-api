package com.example.ecommerce.service;

import com.example.ecommerce.dto.CustomResponse;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    @Autowired
    private Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public CustomResponse<?> createUser(String username, String password, String email) {
        UserRepresentation user = new UserRepresentation();
        user.setUsername(username);
        user.setEmail(email);
        user.setEnabled(true);
        user.setEmailVerified(true);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        passwordCred.setTemporary(false);
        user.setCredentials(Collections.singletonList(passwordCred));

        try (Response response = keycloak.realm(realm).users().create(user)) {
            if (response.getStatus() == 201) {
                return new CustomResponse<>(201, "user created successfully", null);
            } else {
                return new CustomResponse<>(response.getStatus(), "request failed", null);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public CustomResponse<?> loginUser(String username, String password) {
        try {
            AccessTokenResponse token = keycloak.tokenManager().getAccessToken();
            Map<String, String> map = new HashMap<>();
            map.put("access_token", token.getToken());
            map.put("refresh_token", token.getRefreshToken());
            return new CustomResponse<>(201, "user created successfully", map);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
