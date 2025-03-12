// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class, used for signaling that there are not enough products to satisfy an order
 */
public class NotEnoughProductsException extends RuntimeException {
    public NotEnoughProductsException(String message) {
        super(message);
    }
}
