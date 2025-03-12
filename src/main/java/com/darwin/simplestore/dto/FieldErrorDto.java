// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO class representing a field error in case of a jakarta constraint error
 * @param field The associated field
 * @param message The message of the error
 */
public record FieldErrorDto(
        @Schema(description = "The affected field", example = "name")
        String field,
        @Schema(description = "The violation message", example = "Name cannot be null")
        String message) { }
