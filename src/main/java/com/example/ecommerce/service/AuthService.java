package com.example.ecommerce.service;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.Collections;

@Service
public class AuthService {

    @Value("${keycloak.auth-server-url}")
    private String serverUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public Response createUser(String username, String password, String email) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientSecret(clientSecret)
                .clientId(clientId)
                .build();

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

        Response response = keycloak.realm(realm).users().create(user);
        if (response.getStatus() == 201) {
            // User successfully created
            return Response.ok().build();
        } else {
            // Handle error
            return Response.status(response.getStatus()).build();
        }
    }

    public Response loginUser(String username, String password) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .grantType(OAuth2Constants.PASSWORD)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .username(username)
                    .password(password)
                    .build();
            AccessTokenResponse token = keycloak.tokenManager().getAccessToken();
            return Response.ok(token).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.UNAUTHORIZED.getStatusCode(), "Invalid username or password").build();
        }
    }
}
