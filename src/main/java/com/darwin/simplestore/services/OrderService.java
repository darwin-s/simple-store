// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.*;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.entities.CartItem;
import com.darwin.simplestore.entities.Order;
import com.darwin.simplestore.entities.Product;
import com.darwin.simplestore.exceptions.BadOrderStateException;
import com.darwin.simplestore.exceptions.NotEnoughProductsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.CartRepository;
import com.darwin.simplestore.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Set;

/**
 * Service class for working with orders
 */
@Service
@RequiredArgsConstructor
@Validated
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private final ProductService productService;

    /**
     * Return whether all the products are available in the requested quantity
     * @param cartId The id of the cart
     * @return True if there are enough products to satisfy the order, false otherwise
     * @throws ResourceNotFoundException If there is no order with the provided id
     */
    public boolean allProductsAvailable(Long cartId) throws ResourceNotFoundException {
        final Cart cart = cartRepository.findById(cartId).orElseThrow(() -> new ResourceNotFoundException("No cart found with id: " + cartId));
        final Set<CartItem> cartItems = cart.getCartItems();

        for (CartItem cartItem : cartItems) {
            final Long quantity = cartItem.getQuantity();
            final Product product = cartItem.getProduct();

            if (quantity > product.getQuantity()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Place a new order
     * @param cartId The id of the cart to place the order from
     * @return DTO representing the new order
     * @throws ResourceNotFoundException If the specified cart does not exist
     * @throws NotEnoughProductsException If there are not enough products to satisfy the order
     */
    public OrderDto placeOrder(final Long cartId) throws ResourceNotFoundException, NotEnoughProductsException {
        if (!allProductsAvailable(cartId)) {
            throw new NotEnoughProductsException("Not enough products to satisfy order");
        }

        final CartDto cart = cartService.getCart(cartId);
        final Set<CartItemDto> cartItems = cart.cartItems();

        for (CartItemDto cartItem : cartItems) {
            final Long quantity = cartItem.quantity();
            final ProductDto product = cartItem.productDto();
            final ProductDto udpatedProduct = new ProductDto(product.id(),
                    product.name(),
                    product.description(),
                    product.price(),
                    product.quantity() - quantity,
                    product.category()
            );

            productService.updateProductById(udpatedProduct);
        }

        Order order = new Order();
        order.setCart(cartRepository.findById(cartId).get());
        order.setStatus(OrderStatus.AWAITING_PAYMENT);

        cartService.clearCart(cartId);

        return toOrderDto(orderRepository.save(order));
    }

    /**
     * Get an order
     * @param orderId The id of the order
     * @return A DTO representing the order
     * @throws ResourceNotFoundException If no order with the specified id could be found
     */
    public OrderDto getOrder(Long orderId) throws ResourceNotFoundException {
        return toOrderDto(orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("No order found with id: " + orderId)));
    }

    /**
     * Pay for an order
     * @param orderId The id of the order
     * @return A DTO representing the order
     * @throws ResourceNotFoundException If no order with the specified id could be found
     */
    public OrderDto payOrder(final Long orderId) throws ResourceNotFoundException {
        final Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("No order found with id: " + orderId));

        if (order.getStatus() == OrderStatus.AWAITING_PAYMENT) {
            order.setStatus(OrderStatus.DELIVERED);
        }

        return toOrderDto(orderRepository.save(order));
    }

    /**
     * Cancel an order
     * @param orderId The id of the order
     * @throws ResourceNotFoundException If no order with the specified id could be found
     */
    public void cancelOrder(final Long orderId) throws ResourceNotFoundException {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("No order found with id: " + orderId);
        }

        orderRepository.deleteById(orderId);
    }

    /**
     * Finish an order that was delivered
     * @param orderId The id of an order
     * @throws ResourceNotFoundException If no order with the specified id could be found
     * @throws BadOrderStateException If the order is not in the delivered state
     */
    public void finishOrder(final Long orderId) throws ResourceNotFoundException, BadOrderStateException {
        final Order order = orderRepository.findById(orderId).orElseThrow(() -> new ResourceNotFoundException("No order found with id: " + orderId));

        if (order.getStatus() != OrderStatus.DELIVERED) {
            throw new BadOrderStateException("Order is not delivered");
        }

        orderRepository.delete(order);
    }

    /**
     * Convert an order entity to an order DTO object
     * @param order The order entity
     * @return An order DTO representing that entity
     */
    public static OrderDto toOrderDto(Order order) {
        return new OrderDto(order.getId(),
                CartService.toCartDto(order.getCart()),
                order.getStatus());
    }
}
