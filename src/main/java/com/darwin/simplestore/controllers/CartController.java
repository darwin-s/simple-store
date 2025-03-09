package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.CartDto;
import com.darwin.simplestore.dto.CartItemDto;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.CartItemService;
import com.darwin.simplestore.services.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controller for managing carts and their items
 */
@RestController
@RequestMapping("carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final CartItemService cartItemService;

    /**
     * Create a new cart
     * @return DTO representing the created cart
     */
    @PostMapping
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
    public ResponseEntity<Void> addCartItem(@PathVariable final Long cartId,
                                            @RequestParam final Long productId,
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
    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(@PathVariable final Long cartId) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(cartService.getCart(cartId));
    }

    /**
     * Get a certain cart item
     * @param cartId The id of the cart
     * @param productId The id of the product
     * @return Cart item
     * @throws ResourceNotFoundException If the cart or the product could not be found
     */
    @GetMapping("/{cartId}/{productId}")
    public ResponseEntity<CartItemDto> getCartItem(@PathVariable final Long cartId, @PathVariable final Long productId) throws ResourceNotFoundException {
        return ResponseEntity.ok().body(cartItemService.getCartItem(cartId, productId));
    }

    /**
     * Clear a cart
     * @param cartId The id of the cart
     * @return Response object
     * @throws ResourceNotFoundException If the cart could not be found
     */
    @PostMapping("/{cartId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable final Long cartId) throws ResourceNotFoundException {
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
    public ResponseEntity<Void> updateQuantity(@PathVariable final Long cartId,
                                               @PathVariable final Long productId,
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
    public ResponseEntity<Void> deleteCart(@PathVariable final Long cartId) throws ResourceNotFoundException {
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
    public ResponseEntity<Void> deleteItem(@PathVariable final Long cartId, @PathVariable final Long productId) throws ResourceNotFoundException {
        cartItemService.deleteItem(cartId, productId);

        return ResponseEntity.ok().build();
    }
}
