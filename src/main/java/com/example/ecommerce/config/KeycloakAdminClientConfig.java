//package com.example.ecommerce.config;
//
//import org.keycloak.admin.client.Keycloak;
//import org.keycloak.admin.client.KeycloakBuilder;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class KeycloakAdminClientConfig {
//
//    @Value("${keycloak.admin.server-url}")
//    private String serverUrl;
//
//    @Value("${keycloak.admin.realm}")
//    private String realm;
//
//    @Value("${keycloak.admin.client-id}")
//    private String clientId;
//
//    @Value("${keycloak.admin.username}")
//    private String username;
//
//    @Value("${keycloak.admin.password}")
//    private String password;
//
//    @Bean
//    public Keycloak keycloakAdminClient() {
//        return KeycloakBuilder.builder()
//                .serverUrl(serverUrl)
//                .realm(realm)
//                .clientId(clientId)
//                .username(username)
//                .password(password)
//                .build();
//    }
//}