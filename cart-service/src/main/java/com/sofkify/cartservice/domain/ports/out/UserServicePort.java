package com.sofkify.cartservice.domain.ports.out;

import java.util.UUID;

public interface UserServicePort {
    
    boolean validateUser(UUID userId);
    
    record UserInfo(
        UUID id,
        String name,
        String email,
        boolean active
    ) {}
}
