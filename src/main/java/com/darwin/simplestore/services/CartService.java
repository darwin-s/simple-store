package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.CartDto;
import com.darwin.simplestore.dto.CartItemDto;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.CartItemRepository;
import com.darwin.simplestore.repositories.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for managing carts
 */
@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    /**
     * Create a new cart
     * @return The DTO representing the new cart
     */
    public CartDto createCart() {
        final Cart cart = new Cart();
        cart.setCartItems(new HashSet<>());

        return toCartDto(cartRepository.save(cart));
    }

    /**
     * Get a cart by id
     * @param id Id of the cart
     * @return DTO corresponding to the cart
     * @throws ResourceNotFoundException If a cart with such id does not exist
     */
    public CartDto getCart(Long id) throws ResourceNotFoundException {
        return toCartDto(cartRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No cart found with id: " + id)));
    }

    /**
     * Clear a cart, without deleting it
     * @param id Id of the cart
     * @throws ResourceNotFoundException If a cart with such id does not exist
     */
    public void clearCart(Long id) throws ResourceNotFoundException {
        if (!cartRepository.existsById(id)) {
            throw new ResourceNotFoundException("No cart found with id: " + id);
        }

        final Cart cart = cartRepository.findById(id).get();
        cart.getCartItems().clear();
        cartRepository.save(cart);

        cartItemRepository.deleteAllByCartId(id);
    }

    /**
     * Delete a cart, clearing it beforehand
     * @param id Id of the cart
     * @throws ResourceNotFoundException If a cart with such id does not exist
     */
    public void deleteCart(Long id) throws ResourceNotFoundException {
        clearCart(id);

        cartRepository.deleteById(id);
    }

    /**
     * Convert a cart entity to a cart DTO
     * @param cart The entity to convert
     * @return A DTO corresponding to the provided entity
     */
    public static CartDto toCartDto(Cart cart) {
        final Set<CartItemDto> cartItems = cart.getCartItems().stream().
                map(CartItemService::toCartItemDto).
                collect(Collectors.toSet());

        return new CartDto(cart.getId(), cartItems);
    }
}
