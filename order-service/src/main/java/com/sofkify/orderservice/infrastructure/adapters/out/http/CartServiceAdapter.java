package com.sofkify.orderservice.infrastructure.adapters.out.http;

import com.sofkify.orderservice.application.dto.CartResponse;
import com.sofkify.orderservice.domain.ports.out.CartServicePort;
import com.sofkify.orderservice.domain.exception.CartNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;

import java.util.UUID;

@Component
public class CartServiceAdapter implements CartServicePort {

    private final RestTemplate restTemplate;
    private final String cartServiceUrl;

    public CartServiceAdapter(RestTemplate restTemplate,
                              @Value("${cart.service.url:http://localhost:8082}") String cartServiceUrl) {
        this.restTemplate = restTemplate;
        this.cartServiceUrl = cartServiceUrl;
    }

    @Override
    public CartResponse getCartById(UUID cartId) {
        try {
            String url = cartServiceUrl + "/carts/" + cartId;
            return restTemplate.getForObject(url, CartResponse.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new CartNotFoundException(cartId);
        } catch (Exception e) {
            throw new RuntimeException("Error communicating with cart service", e);
        }
    }
}
