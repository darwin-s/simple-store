package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record representing a new product to be added to the
 * @param name The name of the product
 * @param description The description of the product
 * @param price The price of the product
 * @param quantity The initial quantity of the product
 * @param category The category of the product
 */
@Schema(description = "A new product to be added")
public record NewProductDto(
        @Schema(description = "The name of the product", example = "Cheese")
        String name,
        @Schema(description = "A description of the product", example = "The tastiest cheese on the planet!")
        String description,
        @Schema(description = "The price of the product", example = "2.5")
        Double price,
        @Schema(description = "Available quantity of the product", example = "100")
        Long quantity,
        @Schema(description = "The product category", example = "FOOD")
        ProductCategory category) { }
