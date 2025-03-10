package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Enum representing the status of an order
 */
@Schema(description = "The status of an order")
public enum OrderStatus {
    @Schema(description = "The order is awaiting payment")
    AWAITING_PAYMENT,
    @Schema(description = "The order is delivered")
    DELIVERED
}
