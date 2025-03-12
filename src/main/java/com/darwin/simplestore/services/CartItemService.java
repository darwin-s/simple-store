// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.CartItemDto;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.entities.CartItem;
import com.darwin.simplestore.entities.Product;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.CartItemRepository;
import com.darwin.simplestore.repositories.CartRepository;
import com.darwin.simplestore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Service class for cart items
 */
@Service
@RequiredArgsConstructor
@Validated
public class CartItemService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    /**
     * Add a product to a cart
     * @param cartId The id of the cart
     * @param productId The id of the product
     * @param quantity Quantity of the product to add
     * @throws ResourceNotFoundException If either the cart or product could not be found
     */
    public void addProductToCart(final Long cartId, final Long productId, final Long quantity) throws ResourceNotFoundException{
        Cart cart = getCart(cartId);
        Product product = getProduct(productId);
        CartItem cartItem = cart.getCartItems().stream()
                .filter(item -> Objects.equals(item.getProduct().getId(), product.getId()))
                .findFirst().orElse(null);

        if (cartItem == null) {
            cartItem = new CartItem();
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cart.getCartItems().add(cartItem);
        } else {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }

        cartItemRepository.save(cartItem);
        cartRepository.save(cart);
    }

    /**
     * Get an item from the cart
     * @param cartId The id of cart
     * @param productId The id of the product
     * @return DTO representing the requested item in the cart
     * @throws ResourceNotFoundException If either the cart or product could not be found, ot the product is not present in the cart
     */
    public CartItemDto getCartItem(final Long cartId, final Long productId) throws ResourceNotFoundException {
        Cart cart = getCart(cartId);
        Product product = getProduct(productId);
        CartItem cartItem = getCartItem(cart, product);

        return toCartItemDto(cartItem);
    }

    /**
     * Update the quantity of an item in the cart
     * @param cartId The id of cart
     * @param productId The id of the product
     * @param quantity The new quantity
     * @throws ResourceNotFoundException If either the cart or product could not be found, ot the product is not present in the cart
     */
    public void updateItemQuantity(final Long cartId, final Long productId, final Long quantity) throws ResourceNotFoundException {
        Cart cart = getCart(cartId);
        Product product = getProduct(productId);
        CartItem cartItem = getCartItem(cart, product);

        cartItem.setQuantity(quantity);
        cartItemRepository.save(cartItem);
    }

    /**
     * Delete an item from the cart
     * @param cartId The id of cart
     * @param productId The id of the product
     * @throws ResourceNotFoundException If either the cart or product could not be found, ot the product is not present in the cart
     */
    public void deleteItem(final Long cartId, final Long productId) throws ResourceNotFoundException {
        Cart cart = getCart(cartId);
        Product product = getProduct(productId);
        CartItem cartItem = getCartItem(cart, product);

        cart.getCartItems().remove(cartItem);
        cartItemRepository.deleteById(cartItem.getId());
        cartRepository.save(cart);
    }

    /**
     * Get cart from id
     * @param cartId The id of the cart
     * @return The cart entity
     * @throws ResourceNotFoundException If the cart could not be found
     */
    private Cart getCart(final Long cartId) throws ResourceNotFoundException {
        return cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("No cart found with id: " + cartId));
    }

    /**
     * Get product from id
     * @param productId The id of the product
     * @return The product entity
     * @throws ResourceNotFoundException If the product could not be found
     */
    private Product getProduct(final Long productId) throws ResourceNotFoundException {
        return productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("No product found with id: " + productId));
    }

    /**
     * Get the cart item from the cart
     * @param cart The cart where to find the product
     * @param product The product to find
     * @return The cart item entity
     * @throws ResourceNotFoundException If the product is not in the cart
     */
    private CartItem getCartItem(final Cart cart, final Product product) throws ResourceNotFoundException {
        return cart.getCartItems().stream()
                .filter(item -> Objects.equals(item.getProduct().getId(), product.getId()))
                .findFirst().orElseThrow(() -> new ResourceNotFoundException("No product found in the cart with id: " + product.getId()));
    }

    /**
     * Convert a cart item entity to a DTO
     * @param cartItem The cart item entity
     * @return The DTo corresponding to the provided entity
     */
    public static CartItemDto toCartItemDto(CartItem cartItem) {
        return new CartItemDto(cartItem.getId(),
                ProductService.toProductDto(cartItem.getProduct()),
                cartItem.getQuantity());
    }
}
