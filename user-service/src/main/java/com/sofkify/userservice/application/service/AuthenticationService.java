// application/service/AuthenticationService.java
package com.sofkify.userservice.application.service;

import com.sofkify.userservice.application.exception.AccountDisabledException;
import com.sofkify.userservice.application.exception.InvalidCredentialsException;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.domain.model.UserStatus;
import com.sofkify.userservice.domain.ports.out.UserRepositoryPort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    private final UserRepositoryPort userRepository;

    public AuthenticationService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Autentica un usuario basado en email y contraseña
     * @param email Email del usuario
     * @param password Contraseña del usuario
     * @return User autenticado
     * @throws InvalidCredentialsException si las credenciales son inválidas
     * @throws AccountDisabledException si la cuenta está desactivada
     */
    public User authenticate(String email, String password) {
        // 1. Buscar usuario por email
        Optional<User> user = userRepository.findByEmail(email);

        // 2. Si no existe, lanzar excepción de credenciales inválidas
        if (user.isEmpty()) {
            throw new InvalidCredentialsException("Credenciales inválidas: usuario no encontrado");
        }

        // 3. Validar contraseña
        if (!validatePassword(user.orElse(null), password)) {
            throw new InvalidCredentialsException("Credenciales inválidas: contraseña incorrecta");
        }

        // 4. Validar estado del usuario
        if (!validateUserStatus(user.orElse(null))) {
            throw new AccountDisabledException("Cuenta desactivada: el usuario no está activo");
        }

        return user.orElse(null);
    }

    /**
     * Valida que la contraseña coincida
     * @param user Usuario a validar
     * @param password Contraseña a verificar
     * @return true si la contraseña es válida
     */
    private boolean validatePassword(User user, String password) {
        return user.getPassword().equals(password);
    }

    /**
     * Valida que el usuario esté en un estado válido para autenticación
     * @param user Usuario a validar
     * @return true si el usuario está activo
     */
    private boolean validateUserStatus(User user) {
        return user.getStatus() == UserStatus.ACTIVE;
    }
}
