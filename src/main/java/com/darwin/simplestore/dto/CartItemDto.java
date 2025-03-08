package com.darwin.simplestore.dto;

/**
 * Record representing a cart item
 * @param id The id of the cart item
 * @param productDto The DTO of the product
 * @param quantity The quantity of the product
 */
public record CartItemDto(Long id,
                          ProductDto productDto,
                          Long quantity) { }
