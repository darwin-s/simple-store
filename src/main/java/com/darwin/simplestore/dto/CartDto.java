// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

/**
 * Record representing a cart
 * @param id Id of the cart
 * @param cartItems Items inside the cart
 */
@Schema(description = "An existing cart object")
public record CartDto(
        @Schema(description = "The id of the cart", example = "1")
        Long id,
        @Schema(description = "The items inside the cart")
        Set<CartItemDto> cartItems) { }
