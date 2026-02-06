package com.sofkify.userservice.domain.model;

import com.sofkify.userservice.domain.exception.UserValidationException;

import java.time.LocalDateTime;
import java.util.UUID;

// domain/model/User.java
public class User {
    private String id;
    private String email;
    private String password;
    private String name;
    private UserRole role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor privado para forzar uso de factory methods
    private User(String id, String email, String password, String name, UserRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.role = role;
        this.status = UserStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Factory method
    public static User create(String email, String password, String name) {
        validateEmail(email);
        validatePassword(password);
        validateName(name);

        // 🔥 REGLA DE NEGOCIO: Solo se crean CLIENTES por defecto
        return new User(
                UUID.randomUUID().toString(),
                email,
                password,
                name,
                UserRole.CLIENTE  // 🔒 Siempre CLIENTE al crear
        );
    }

    // Comportamiento del dominio
    public void updateProfile(String newName, String newEmail) {
        validateName(newName);
        validateEmail(newEmail);
        this.name = newName;
        this.email = newEmail;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void promoteToAdmin() {
        // 🔥 REGLA DE NEGOCIO: Solo CLIENTES pueden ser promovidos
        if (this.role != UserRole.CLIENTE) {
            throw new UserValidationException("Solo clientes pueden ser promovidos a ADMIN");
        }

        this.role = UserRole.ADMIN;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public boolean isClient() {
        return this.role == UserRole.CLIENTE;
    }


    public boolean isActive() {
        return this.status == UserStatus.ACTIVE;
    }

    // Validaciones de negocio (reglas puras)
    private static void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new UserValidationException("Email inválido");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new UserValidationException("Password debe tener al menos 8 caracteres");
        }
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new UserValidationException("Nombre es requerido");
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getName() {
        return name;
    }

    public UserStatus getStatus() {
        return status;
    }

    public String getPassword() {
        return password;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ... otros getters sin setters (inmutabilidad)
}
