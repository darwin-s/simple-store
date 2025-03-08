package com.darwin.simplestore.dto;

import java.util.Set;

/**
 * Record representing a cart
 * @param id Id of the cart
 * @param cartItems Items inside the cart
 */
public record CartDto(Long id,
                      Set<CartItemDto> cartItems) { }
