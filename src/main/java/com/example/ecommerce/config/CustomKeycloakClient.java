package com.example.ecommerce.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomKeycloakClient {

    @Autowired
    private KeycloakProperties keycloakProps;

    @Bean
    public Keycloak keycloakClient() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProps.getAuthServerUrl())
                .realm(keycloakProps.getRealm())
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientSecret(keycloakProps.getClientSecret())
                .clientId(keycloakProps.getClientId())
                .build();
    }
}
