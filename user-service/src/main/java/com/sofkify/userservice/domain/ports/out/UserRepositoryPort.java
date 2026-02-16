package com.sofkify.userservice.domain.ports.out;

import com.sofkify.userservice.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {

    // Guardar usuario (crear o actualizar)
    User save(User user);

    // Buscar por ID
    Optional<User> findById(String id);

    // Buscar por email
    Optional<User> findByEmail(String email);

    // Verificar existencia por email
    boolean existsByEmail(String email);

    // Eliminar usuario
    void deleteById(String id);

}
