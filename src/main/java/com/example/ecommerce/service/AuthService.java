package com.example.ecommerce.service;

import com.example.ecommerce.dto.CustomResponse;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
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

    @Autowired
    private KeycloakBuilder keycloakClientBuilder;

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public CustomResponse<?> createUser(String password, String email, String firstName, String lastName) {
        UserRepresentation userRepresentation = getUserRepresentation(password, email, firstName, lastName);

        try (Response response = keycloak.realm(realm).users().create(userRepresentation)) {
            if (response.getStatus() == 201) {
                return new CustomResponse<>(201, "userRepresentation created successfully", null);
            } else {
                return new CustomResponse<>(response.getStatus(), "request failed", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static UserRepresentation getUserRepresentation(String email, String password, String firstName, String lastName) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setUsername(email);
        userRepresentation.setEmail(email);
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(firstName);
        userRepresentation.setLastName(lastName);
        userRepresentation.setEmailVerified(true);

        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(password);
        passwordCred.setTemporary(false);
        userRepresentation.setCredentials(Collections.singletonList(passwordCred));
        return userRepresentation;
    }

    public CustomResponse<?> loginUser(String email, String password) {
        try {
            AccessTokenResponse token = keycloakClientBuilder
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(email)
                    .password(password)
                    .build().tokenManager().getAccessToken();
            Map<String, String> map = new HashMap<>();
            map.put("access_token", token.getToken());
            map.put("refresh_token", token.getRefreshToken());
            return new CustomResponse<>(201, "user created successfully", map);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
