// application/exception/AccountDisabledException.java
package com.sofkify.userservice.application.exception;

/**
 * Excepción lanzada cuando se intenta autenticar un usuario con cuenta desactivada
 */
public class AccountDisabledException extends RuntimeException {
    
    public AccountDisabledException(String message) {
        super(message);
    }
    
    public AccountDisabledException(String message, Throwable cause) {
        super(message, cause);
    }
}
