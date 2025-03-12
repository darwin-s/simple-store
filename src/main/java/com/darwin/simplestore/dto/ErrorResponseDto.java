// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Dto class for error responses
 */
@Data
public class ErrorResponseDto {
    @Schema(description = "Timestamp of the error", example = "2025-03-12 12:03:12")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime timestamp = LocalDateTime.now();
    @Schema(description = "Error status", example = "404")
    private Integer status;
    @Schema(description = "Error type", example = "Not Found")
    private String error;
    @Schema(description = "Error message", example = "The product could not be found")
    private String message;
    @Schema(description = "Affected endpoint", example = "/products/1")
    private String path;
    @Schema(description = "Affected fields, in case of a constraint violation error")
    private final List<FieldErrorDto> fieldErrors = new ArrayList<>();

    /**
     * Add a field error
     * @param fieldError Field error to add
     */
    public void addFieldError(final FieldErrorDto fieldError) {
        fieldErrors.add(fieldError);
    }

    /**
     * Set the status and error fields from a HttpStatus object
     * @param httpStatus The http status object
     */
    public void setFromHttpStatus(final HttpStatus httpStatus) {
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
    }
}
