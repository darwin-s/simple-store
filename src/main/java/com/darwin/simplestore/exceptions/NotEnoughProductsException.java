package com.darwin.simplestore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class, used for signaling that there are not enough products to satisfy an order
 */
@ResponseStatus(code = HttpStatus.CONFLICT, reason = "Not enough products to satisfy order")
public class NotEnoughProductsException extends RuntimeException {
    public NotEnoughProductsException(String message) {
        super(message);
    }
}
