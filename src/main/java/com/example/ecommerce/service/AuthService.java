package com.example.ecommerce.service;

import com.example.ecommerce.config.KeycloakProperties;
import com.example.ecommerce.dto.CustomResponse;
import com.example.ecommerce.dto.ErrorResponse;
import com.example.ecommerce.dto.LoginRequest;
import com.example.ecommerce.dto.SignupRequest;
import com.example.ecommerce.entity.AppUser;
import com.example.ecommerce.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
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
    private KeycloakProperties keycloakProps;

    @Autowired
    private RestTemplate restTemplate;

    public CustomResponse<?> createUser(SignupRequest request) {
        UserRepresentation userRepresentation = getUserRepresentation(request.email(), request.password(), request.firstName(), request.lastName());
        String userId = null;
        try (Response response = keycloak.realm(keycloakProps.getRealm()).users().create(userRepresentation)) {
            if (response.getStatus() == HttpStatus.CREATED.value()) {
                String location = response.getHeaderString("Location");
                userId = location.substring(location.lastIndexOf("/") + 1);
                userRepository.save(AppUser.builder().id(userId).build());
                return new CustomResponse<>(HttpStatus.CREATED.value(), "user created successfully", null);
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
                keycloak.realm(keycloakProps.getRealm()).users().delete(userId);
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

    public CustomResponse<?> loginUser(LoginRequest loginRequest, HttpServletResponse httpServletResponse) {
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token", keycloakProps.getAuthServerUrl(), keycloakProps.getRealm());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.PASSWORD);
        formData.add(OAuth2Constants.USERNAME, loginRequest.email());
        formData.add(OAuth2Constants.PASSWORD, loginRequest.password());
        formData.add(OAuth2Constants.CLIENT_ID, keycloakProps.getClientId());
        formData.add(OAuth2Constants.CLIENT_SECRET, keycloakProps.getClientSecret());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        try {
            ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    AccessTokenResponse.class
            );
            AccessTokenResponse token = response.getBody();
            Map<String, String> map = new HashMap<>();
            assert token != null;
            map.put(OAuth2Constants.ACCESS_TOKEN, token.getToken());
            map.put(OAuth2Constants.REFRESH_TOKEN, token.getRefreshToken());

            Cookie refreshCookie = new Cookie(OAuth2Constants.REFRESH_TOKEN, token.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge((int) Duration.ofDays(7).getSeconds()); // 7 days

            httpServletResponse.addCookie(refreshCookie);
            return new CustomResponse<>(HttpStatus.OK.value(), "refreshed successfully", map);
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "Refresh failed", null);
        }
//        try {
//            AccessTokenResponse token = keycloakClientBuilder
//                    .grantType(OAuth2Constants.PASSWORD)
//                    .username(request.email())
//                    .password(request.password())
//                    .build().tokenManager().getAccessToken();
//            Map<String, String> map = new HashMap<>();
//            map.put("access_token", token.getToken());
//            map.put("refresh_token", token.getRefreshToken());
//            return new CustomResponse<>(HttpStatus.OK.value(), "login successfully", map);
//        } catch (BadRequestException e) {
//            return new CustomResponse<>(HttpStatus.BAD_REQUEST.value(), "invalid parameters or can't process", null);
//        } catch (NotAuthorizedException e) {
//            return new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "email or password is incorrect", null);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
    }

    public CustomResponse<?> refreshToken(String refreshToken, HttpServletResponse httpServletResponse) {
        if (refreshToken == null || refreshToken.isBlank()) {
            return new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(),
                    "Refresh token is missing or expired", null);
        }
        String tokenUrl = String.format("%s/realms/%s/protocol/openid-connect/token",
                keycloakProps.getAuthServerUrl(),
                keycloakProps.getRealm());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add(OAuth2Constants.GRANT_TYPE, OAuth2Constants.REFRESH_TOKEN);
        formData.add(OAuth2Constants.REFRESH_TOKEN, refreshToken);
        formData.add(OAuth2Constants.CLIENT_ID, keycloakProps.getClientId());
        formData.add(OAuth2Constants.CLIENT_SECRET, keycloakProps.getClientSecret());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);
        try {
            ResponseEntity<AccessTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    request,
                    AccessTokenResponse.class
            );
            AccessTokenResponse token = response.getBody();
            Map<String, String> map = new HashMap<>();
            assert token != null;
            map.put(OAuth2Constants.ACCESS_TOKEN, token.getToken());
            map.put(OAuth2Constants.REFRESH_TOKEN, token.getRefreshToken());

            Cookie refreshCookie = new Cookie(OAuth2Constants.REFRESH_TOKEN, token.getRefreshToken());
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true);
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge((int) Duration.ofDays(7).getSeconds()); // 7 days

            httpServletResponse.addCookie(refreshCookie);
            return new CustomResponse<>(HttpStatus.OK.value(), "refreshed successfully", map);
        } catch (Exception e) {
            e.printStackTrace();
            return new CustomResponse<>(HttpStatus.UNAUTHORIZED.value(), "Refresh failed", null);
        }
    }
}
