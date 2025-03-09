package com.darwin.simplestore.dto;

/**
 * Record representing a DTO for the order entity
 * @param id Id of the order
 * @param cartDto Dto representing teh cart associated with the order
 * @param status The status of the order
 */
public record OrderDto(Long id,
                       CartDto cartDto,
                       OrderStatus status) { }
