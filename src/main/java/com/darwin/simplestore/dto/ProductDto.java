package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record representing an existing product in the repository
 * @param id The id of the product
 * @param name The name of the product
 * @param description The description of the product
 * @param price The price of the product
 * @param quantity The quantity of the product
 * @param category The category of the product
 */
@Schema(description = "An existing product description")
public record ProductDto(
        @Schema(description = "The id of the product", example = "1")
        Long id,
        @Schema(description = "The name of the product", example = "Cheese")
        String name,
        @Schema(description = "A description of the product", example = "The tastiest cheese on the planet!")
        String description,
        @Schema(description = "The price of the product", example = "2.5")
        Double price,
        @Schema(description = "Available quantity of the product", example = "100")
        Long quantity,


        ProductCategory category) { }
