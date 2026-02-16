package com.sofkify.userservice.infrastructure.mapper;

import com.sofkify.userservice.application.dto.UserResponse;
import com.sofkify.userservice.domain.model.User;
import com.sofkify.userservice.infrastructure.adapters.out.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // Entity → Domain
    public User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .password(entity.getPassword())
                .name(entity.getName())
                .role(entity.getRole())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // Domain → Entity
    public UserEntity toEntity(User user) {
        if (user == null) {
            return null;
        }

        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getRole(),
                user.getStatus(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
    // Domain → DTO
    public UserResponse toDto(User user) {
        if (user == null) {
            return null;
        }

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    // Lista de Entities → Lista de Domains
    public java.util.List<User> toDomainList(java.util.List<UserEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(java.util.stream.Collectors.toList());
    }

    // Lista de Domains → Lista de Entities
    public java.util.List<UserEntity> toEntityList(java.util.List<User> users) {
        return users.stream()
                .map(this::toEntity)
                .collect(java.util.stream.Collectors.toList());
    }
}