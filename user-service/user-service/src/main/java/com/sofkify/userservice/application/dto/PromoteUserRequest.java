package com.sofkify.userservice.application.dto;

import jakarta.validation.constraints.NotBlank;

public class PromoteUserRequest {

    @NotBlank(message = "ID de usuario es requerido")
    private String userId;

    // Getters y setters
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
