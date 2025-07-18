package com.example.ecommerce.service;

import com.example.ecommerce.dto.CustomResponse;
import com.example.ecommerce.dto.ErrorResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.SignupRequest;
import com.example.ecommerce.entity.AppUser;
import com.example.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotAuthorizedException;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private Keycloak keycloak;

    @Autowired
    private KeycloakBuilder keycloakClientBuilder;

    @Value("${keycloak.realm}")
    private String realm;

    public CustomResponse<?> createUser(SignupRequest request) {
        UserRepresentation userRepresentation = getUserRepresentation(request.email(), request.password(), request.firstName(), request.lastName());
        String userId = null;
        try (Response response = keycloak.realm(realm).users().create(userRepresentation)) {
            if (response.getStatus() == 201) {
                String location = response.getHeaderString("Location");
                userId = location.substring(location.lastIndexOf("/") + 1);
                userRepository.save(AppUser.builder().id(userId).build());
                return new CustomResponse<>(201, "user created successfully", null);
            } else {
                String message = response.readEntity(String.class);
                ObjectMapper mapper = new ObjectMapper();
                ErrorResponse err = mapper.readValue(message, ErrorResponse.class);
                String errorMessage = err.errorMessage();
                return new CustomResponse<>(response.getStatus(), errorMessage, null);
            }
        } catch (Exception e) {
            if (userId != null) {
                // Roll back Keycloak user manually
                keycloak.realm(realm).users().delete(userId);
            }
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

    public CustomResponse<?> loginUser(LoginRequest request) {
        try {
            AccessTokenResponse token = keycloakClientBuilder
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(request.email())
                    .password(request.password())
                    .build().tokenManager().getAccessToken();
            Map<String, String> map = new HashMap<>();
            map.put("access_token", token.getToken());
            map.put("refresh_token", token.getRefreshToken());
            return new CustomResponse<>(201, "user created successfully", map);
        } catch (BadRequestException e) {
            return new CustomResponse<>(HttpStatus.BAD_REQUEST.value(), "invalid parameters or can't process", null);
        } catch (NotAuthorizedException e) {
            return new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "email or password is incorrect", null);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
