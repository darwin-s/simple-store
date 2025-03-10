package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.CartDto;
import com.darwin.simplestore.dto.CartItemDto;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.CartItemService;
import com.darwin.simplestore.services.CartService;
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
 * Controller for managing carts and their items
 */
@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
@Tag(name = "Carts", description = "Endpoints for managing carts")
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;

    /**
     * Create a new cart
     * @return DTO representing the created cart
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create cart", description = "Creates a new empty cart")
    public ResponseEntity<CartDto> createCart() {
        final CartDto cartDto = cartService.createCart();
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{cartId}")
                .buildAndExpand(cartDto.id())
                .toUri();

        return ResponseEntity.created(location).body(cartDto);
    }

    /**
     * Add an item to the cart
     * @param cartId The id of the cart
     * @param productId The id of the product
     * @param quantity The quantity to be added
     * @return Response object
     * @throws ResourceNotFoundException If the cart or the product could not be found
     */
    @PostMapping("/{cartId}/add")
    @Operation(summary = "Add cart item", description = "Add a new item to a cart")
    public ResponseEntity<Void> addCartItem(
            @Parameter(description = "The id of the cart", example = "1")
            @PathVariable final Long cartId,
            @Parameter(description = "The id of the product", example = "1")
            @RequestParam final Long productId,
            @Parameter(description = "The quantity of the product to add", example = "5")
            @RequestParam final Long quantity) throws ResourceNotFoundException {

        cartItemService.addProductToCart(cartId, productId, quantity);
        return ResponseEntity.ok().build();
    }

    /**
     * Get a cart
     * @param cartId the id of the cart
     * @return DTO representing the cart
     * @throws ResourceNotFoundException If the cart could not be found
     */
    @GetMapping(value = "/{cartId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get cart", description = "Retrieve and return an existing cart")
    public ResponseEntity<CartDto> getCart(
            @Parameter(description = "The id of the cart", example = "1")
            @PathVariable final Long cartId) throws ResourceNotFoundException {

        return ResponseEntity.ok().body(cartService.getCart(cartId));
    }

    /**
     * Get a certain cart item
     * @param cartId The id of the cart
     * @param productId The id of the product
     * @return Cart item
     * @throws ResourceNotFoundException If the cart or the product could not be found
     */
    @GetMapping(value = "/{cartId}/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get cart item", description = "Retrieve and return an item from teh cart")
    public ResponseEntity<CartItemDto> getCartItem(
            @Parameter(description = "The id of the cart", example = "1")
            @PathVariable final Long cartId,
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(cartItemService.getCartItem(cartId, productId));
    }

    /**
     * Clear a cart
     * @param cartId The id of the cart
     * @return Response object
     * @throws ResourceNotFoundException If the cart could not be found
     */
    @PostMapping("/{cartId}/clear")
    @Operation(summary = "Clear cart", description = "Clears a cart, and leaves it empty")
    public ResponseEntity<Void> clearCart(
            @Parameter(description = "The id of the cart", example = "1")
            @PathVariable final Long cartId) throws ResourceNotFoundException {

        cartService.clearCart(cartId);

        return ResponseEntity.ok().build();
    }

    /**
     * Update cart item quantity
     * @param cartId The id of the cart
     * @param productId The id of the product
     * @param quantity The new quantity of the product
     * @return Response object
     * @throws ResourceNotFoundException If the cart or the product could not be found
     */
    @PutMapping("/{cartId}/{productId}")
    @Operation(summary = "Update quantity", description = "Updates the quantity of a cart item")
    public ResponseEntity<Void> updateQuantity(
            @Parameter(description = "The id of the cart", example = "1")
            @PathVariable final Long cartId,
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId,
            @Parameter(description = "The new quantity of the product", example = "4")
            @RequestParam final Long quantity) throws ResourceNotFoundException {

        cartItemService.updateItemQuantity(cartId, productId, quantity);

        return ResponseEntity.ok().build();
    }

    /**
     * Delete a cart
     * @param cartId The id of the cart
     * @return response object
     * @throws ResourceNotFoundException If the cart could not be found
     */
    @DeleteMapping("/{cartId}")
    @Operation(summary = "Delete cart", description = "Clear and then delete a cart")
    public ResponseEntity<Void> deleteCart(
            @Parameter(description = "The id of the cart", example = "1")
            @PathVariable final Long cartId) throws ResourceNotFoundException {

        cartService.deleteCart(cartId);

        return ResponseEntity.ok().build();
    }

    /**
     * Delete an item from the cart
     * @param cartId The id of the cart
     * @param productId The id of the product
     * @return Response object
     * @throws ResourceNotFoundException If the cart or the product could not be found
     */
    @DeleteMapping("/{cartId}/{productId}")
    @Operation(summary = "Delete cart item", description = "Delete a product from the cart")
    public ResponseEntity<Void> deleteItem(
            @Parameter(description = "The id of the cart", example = "1")
            @PathVariable final Long cartId,
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId) throws ResourceNotFoundException {

        cartItemService.deleteItem(cartId, productId);

        return ResponseEntity.ok().build();
    }
}
