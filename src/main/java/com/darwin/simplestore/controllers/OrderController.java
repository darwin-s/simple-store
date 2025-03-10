package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.OrderDto;
import com.darwin.simplestore.exceptions.BadOrderStateException;
import com.darwin.simplestore.exceptions.NotEnoughProductsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controller for managing orders
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Endpoints for managing orders")
public class OrderController {
    private final OrderService orderService;

    /**
     * Place an order
     * @param cartId The id of the cart from which the order will be placed
     * @return DTO representing the order
     * @throws ResourceNotFoundException If the cart could not be found
     * @throws NotEnoughProductsException If there are not enough products to satisfy the order
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Place order", description = "Places a new order from a cart, and clears teh cart")
    public ResponseEntity<OrderDto> placeOrder(
            @Parameter(description = "The id of the cart", example = "1")
            @RequestParam final Long cartId) throws ResourceNotFoundException, NotEnoughProductsException {

        final OrderDto orderDto = orderService.placeOrder(cartId);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{orderId}")
                .buildAndExpand(orderDto.id())
                .toUri();

        return ResponseEntity.created(location).body(orderDto);
    }

    /**
     * Get an order
     * @param orderId The id of the order
     * @return The DTO representing the order
     * @throws ResourceNotFoundException If the order could not be found
     */
    @GetMapping(value = "/{orderId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get order", description = "Retrieve and return an existing order")
    public ResponseEntity<OrderDto> getOrder(
            @Parameter(description = "The id of the order", example = "1")
            @PathVariable final Long orderId) throws ResourceNotFoundException {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    /**
     * Pay for an order
     * @param orderId The id of the order
     * @return Response object
     * @throws ResourceNotFoundException If the order could not be found
     */
    @PostMapping("/{orderId}/pay")
    @Operation(summary = "Pay order", description = "pay for an order, then change it's status to delivered")
    public ResponseEntity<Void> payOrder(
            @Parameter(description = "The id of the order", example = "1")
            @PathVariable final Long orderId) throws ResourceNotFoundException {

        orderService.payOrder(orderId);

        return ResponseEntity.ok().build();
    }

    /**
     * Cancel an order
     * @param orderId The id of the order
     * @return Response object
     * @throws ResourceNotFoundException If the order could not be found
     */
    @DeleteMapping("/{orderId}")
    @Operation(summary = "Cancel order", description = "Cancel and delete an order")
    public ResponseEntity<Void> cancelOrder(
            @Parameter(description = "The id of the order", example = "1")
            @PathVariable final Long orderId) throws ResourceNotFoundException {

        orderService.cancelOrder(orderId);

        return ResponseEntity.ok().build();
    }

    /**
     * Finish an order
     * @param orderId The id of the order
     * @return Response object
     * @throws ResourceNotFoundException If the order could not be found
     * @throws BadOrderStateException If the order was not delivered yet
     */
    @PostMapping("/{orderId}/finish")
    @Operation(summary = "Finish an order", description = "Finish an order that is delivered")
    public ResponseEntity<Void> finishOrder(
            @Parameter(description = "The id of the order", example = "1")
            @PathVariable final Long orderId) throws ResourceNotFoundException, BadOrderStateException {

        orderService.finishOrder(orderId);

        return ResponseEntity.ok().build();
    }
}
