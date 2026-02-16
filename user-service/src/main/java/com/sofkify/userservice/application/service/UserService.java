// application/service/UserService.java
package com.sofkify.userservice.application.service;

import com.sofkify.userservice.application.exception.AccountDisabledException;
import com.sofkify.userservice.application.exception.InvalidCredentialsException;
import com.sofkify.userservice.application.exception.UserAlreadyExistsException;
import com.sofkify.userservice.application.exception.UserNotFoundException;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.model.UserRole;
import com.sofkify.userservice.domain.model.UserStatus;
import com.sofkify.userservice.domain.ports.in.UserServicePort;
import com.sofkify.userservice.domain.ports.out.UserRepositoryPort;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserServicePort {

    private final UserRepositoryPort userRepository;
    private final AuthenticationService authenticationService;

    // Inyección de dependencias del Port OUT y AuthenticationService
    public UserService(UserRepositoryPort userRepository, AuthenticationService authenticationService) {
        this.userRepository = userRepository;
        this.authenticationService = authenticationService;
    }

    @Override
    public User createUser(String email, String password, String name) {
        // 1. Verificar si ya existe el email
        if (userRepository.existsByEmail(email)) {
            throw new UserAlreadyExistsException("Email ya registrado: " + email);
        }

        // 2. Crear usuario (el dominio valida)
        User newUser = User.create(email, password, name);

        // 3. Guardar en base de datos
        return userRepository.save(newUser);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public User updateProfile(String userId, String newName, String newEmail) {
        // 1. Buscar usuario existente
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId));

        // 2. Verificar si el nuevo email ya está en uso por otro usuario
        if (!existingUser.getEmail().equals(newEmail) &&
                userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistsException("Email ya en uso: " + newEmail);
        }

        // 3. Actualizar perfil (el dominio valida)
        existingUser.updateProfile(newName, newEmail);

        // 4. Guardar cambios
        return userRepository.save(existingUser);
    }

    @Override
    public void deactivateUser(String userId) {
        // 1. Buscar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId));

        // 2. Desactivar (el dominio maneja el estado)
        user.deactivate();

        // 3. Guardar cambios
        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User promoteToAdmin(String userId) {
        // 1. Buscar usuario existente
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado: " + userId));

        // 2. Promover a administrador (el dominio valida reglas)
        user.promoteToAdmin();

        // 3. Guardar cambios
        return userRepository.save(user);
    }

    @Override
    public Optional<User> authenticateUser(String email, String password) {
        try {
            User authenticatedUser = authenticationService.authenticate(email, password);
            return Optional.of(authenticatedUser);
        } catch (InvalidCredentialsException | AccountDisabledException e) {
            return Optional.empty();
        }
    }
}
