// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception class, used for signaling a bad state of an order
 */
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad order state")
public class BadOrderStateException extends RuntimeException {
    public BadOrderStateException(String message) {
        super(message);
    }
}
