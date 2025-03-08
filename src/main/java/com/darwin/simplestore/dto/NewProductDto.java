package com.darwin.simplestore.dto;

/**
 * Record representing a new product to be added to the
 * @param name The name of the product
 * @param description The description of the product
 * @param price The price of the product
 * @param quantity The initial quantity of the product
 * @param category The category of the product
 */
public record NewProductDto(String name,
                            String description,
                            Double price,
                            Long quantity,
                            ProductCategory category) { }
