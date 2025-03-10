// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record representing a DTO for the order entity
 * @param id Id of the order
 * @param cartDto Dto representing teh cart associated with the order
 * @param status The status of the order
 */
@Schema(description = "An existing order")
public record OrderDto(
        @Schema(description = "The id of the order", example = "1")
        Long id,
        @Schema(description = "The cart associated with the order")
        CartDto cartDto,
        @Schema(description = "The status of the order", example = "AWAITING_PAYMENT")
        OrderStatus status) { }
