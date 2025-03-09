package com.darwin.simplestore.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad order state")
public class BadOrderStateException extends RuntimeException {
    public BadOrderStateException(String message) {
        super(message);
    }
}
