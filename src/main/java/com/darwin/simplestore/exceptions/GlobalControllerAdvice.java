// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.exceptions;

import com.darwin.simplestore.dto.ErrorResponseDto;
import com.darwin.simplestore.dto.FieldErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * The global controller advice class for exception handling
 */
@RestControllerAdvice
public class GlobalControllerAdvice {

    /**
     * Handle constraint violation exceptions
     * @param e The constraint violation exception
     * @param request The associated request
     * @return Error DTO
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleConstraintViolationException(
            final ConstraintViolationException e,
            final HttpServletRequest request) {
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto();

        errorResponseDto.setFromHttpStatus(HttpStatus.BAD_REQUEST);
        errorResponseDto.setMessage("Validation Error");
        errorResponseDto.setPath(request.getRequestURI());

        for (ConstraintViolation<?> violation : e.getConstraintViolations()) {
            final FieldErrorDto fieldError = new FieldErrorDto(
                    violation.getPropertyPath().toString(),
                    violation.getMessage()
            );

            errorResponseDto.addFieldError(fieldError);
        }

        return errorResponseDto;
    }

    /**
     * Handle bad order state exception
     * @param e The exception
     * @param request The associated request
     * @return Error DTO
     */
    @ExceptionHandler(BadOrderStateException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadOrderStateException(
            final BadOrderStateException e,
            final HttpServletRequest request) {
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto();

        errorResponseDto.setFromHttpStatus(HttpStatus.BAD_REQUEST);
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setPath(request.getRequestURI());

        return errorResponseDto;
    }

    /**
     * Handle not enough products exception
     * @param e The exception
     * @param request The associated request
     * @return Error DTO
     */
    @ExceptionHandler(NotEnoughProductsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleNotEnoughProductsException(
            final NotEnoughProductsException e,
            final HttpServletRequest request) {
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto();

        errorResponseDto.setFromHttpStatus(HttpStatus.BAD_REQUEST);
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setPath(request.getRequestURI());

        return errorResponseDto;
    }

    /**
     * Handle resource exists exception
     * @param e The exception
     * @param request The associated request
     * @return Error DTO
     */
    @ExceptionHandler(ResourceExistsException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleResourceExistsException(
            final ResourceExistsException e,
            final HttpServletRequest request) {
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto();

        errorResponseDto.setFromHttpStatus(HttpStatus.BAD_REQUEST);
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setPath(request.getRequestURI());

        return errorResponseDto;
    }

    /**
     * Handle resource not found exception
     * @param e The exception
     * @param request The associated exception
     * @return Error DTO
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleResourceNotFoundException(
            final ResourceNotFoundException e,
            final HttpServletRequest request) {
        final ErrorResponseDto errorResponseDto = new ErrorResponseDto();

        errorResponseDto.setFromHttpStatus(HttpStatus.NOT_FOUND);
        errorResponseDto.setMessage(e.getMessage());
        errorResponseDto.setPath(request.getRequestURI());

        return errorResponseDto;
    }
}
