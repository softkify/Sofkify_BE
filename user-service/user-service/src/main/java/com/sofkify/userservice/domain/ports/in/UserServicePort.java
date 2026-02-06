package com.sofkify.userservice.domain.ports.in;

import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.model.UserRole;

import java.util.Optional;

public interface UserServicePort {

    // Crear nuevo usuario (siempre se crea como CLIENTE por regla de negocio)
    User createUser(String email, String password, String name);

    // Buscar usuario por email
    User findByEmail(String email);

    // Buscar usuario por ID
    Optional<User> findById(String id);

    // Actualizar perfil de usuario
    User updateProfile(String userId, String newName, String newEmail);

    // Desactivar usuario
    void deactivateUser(String userId);

    // Verificar si existe usuario con ese email
    boolean existsByEmail(String email);

    // Promover cliente a administrador
    User promoteToAdmin(String userId);

    // Autenticar usuario (login)
    Optional<User> authenticateUser(String email, String password);

}
