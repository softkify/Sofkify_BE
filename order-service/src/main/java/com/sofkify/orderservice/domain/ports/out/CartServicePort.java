package com.sofkify.orderservice.domain.ports.out;

import com.sofkify.orderservice.application.dto.CartResponse;
import java.util.UUID;

public interface CartServicePort {
    CartResponse getCartById(UUID cartId);
}
