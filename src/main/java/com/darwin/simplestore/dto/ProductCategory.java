// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the category of a product
 */
@Schema(description = "The category of a product")
public enum ProductCategory {
    FOOD,
    CLOTHES,
    ELECTRONICS,
    OTHER
}
