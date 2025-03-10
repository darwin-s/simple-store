// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record representing a cart item
 * @param id The id of the cart item
 * @param productDto The DTO of the product
 * @param quantity The quantity of the product
 */
@Schema(description = "An existing cart item")
public record CartItemDto(
        @Schema(description = "The id of the cart item", example = "1")
        Long id,
        @Schema(description = "The product associated with the cart item")
        ProductDto productDto,
        @Schema(description = "The quantity of the product")
        Long quantity) { }
