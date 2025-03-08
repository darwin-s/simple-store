package com.darwin.simplestore.dto;

/**
 * Record representing an existing product in the repository
 * @param id The id of the product
 * @param name The name of the product
 * @param description The description of the product
 * @param price The price of the product
 * @param quantity The quantity of the product
 * @param category The category of the product
 */
public record ProductDto(Long id,
                         String name,
                         String description,
                         Double price,
                         Long quantity,
                         ProductCategory category) { }
