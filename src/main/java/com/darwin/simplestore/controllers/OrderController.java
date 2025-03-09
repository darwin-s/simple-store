package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.OrderDto;
import com.darwin.simplestore.exceptions.BadOrderStateException;
import com.darwin.simplestore.exceptions.NotEnoughProductsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.OrderService;
import lombok.RequiredArgsConstructor;
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
public class OrderController {
    private final OrderService orderService;

    /**
     * Place an order
     * @param cartId The id of the cart from which the order will be placed
     * @return DTO representing the order
     * @throws ResourceNotFoundException If the cart could not be found
     * @throws NotEnoughProductsException If there are not enough products to satisfy the order
     */
    @PostMapping
    public ResponseEntity<OrderDto> placeOrder(@RequestParam final Long cartId) throws ResourceNotFoundException, NotEnoughProductsException {
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
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable final Long orderId) throws ResourceNotFoundException {
        return ResponseEntity.ok(orderService.getOrder(orderId));
    }

    /**
     * Pay for an order
     * @param orderId The id of the order
     * @return Response object
     * @throws ResourceNotFoundException If the order could not be found
     */
    @PostMapping("/{orderId}/pay")
    public ResponseEntity<Void> payOrder(@PathVariable final Long orderId) throws ResourceNotFoundException {
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
    public ResponseEntity<Void> cancelOrder(@PathVariable final Long orderId) throws ResourceNotFoundException {
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
    public ResponseEntity<Void> finishOrder(@PathVariable final Long orderId) throws ResourceNotFoundException, BadOrderStateException {
        orderService.finishOrder(orderId);

        return ResponseEntity.ok().build();
    }
}
