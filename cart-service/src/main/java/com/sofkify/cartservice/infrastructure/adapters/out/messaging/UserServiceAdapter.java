package com.sofkify.cartservice.infrastructure.adapters.out.messaging;

import com.sofkify.cartservice.domain.ports.out.UserServicePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class UserServiceAdapter implements UserServicePort {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceAdapter(RestTemplate restTemplate,
                            @Value("${user.service.url:http://localhost:8080/api}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    @Override
    public boolean validateUser(UUID userId) {
        try {
            String url = userServiceUrl + "/users/" + userId;
            UserResponse response = restTemplate.getForObject(url, UserResponse.class);
            
            if (response == null) {
                return false;
            }
            
            return response.active();
        } catch (Exception e) {
            // Si el user-service no está disponible o el usuario no existe
            return false;
        }
    }

    // DTO para respuesta del user-service
    private static class UserResponse {
        private UUID id;
        private String name;
        private String email;
        private String status;

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public boolean active() {
            return "ACTIVE".equals(status);
        }
    }
}
