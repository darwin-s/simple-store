// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.OrderDto;
import com.darwin.simplestore.dto.OrderStatus;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.dto.ProductDto;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.entities.CartItem;
import com.darwin.simplestore.entities.Order;
import com.darwin.simplestore.entities.Product;
import com.darwin.simplestore.exceptions.BadOrderStateException;
import com.darwin.simplestore.exceptions.NotEnoughProductsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.CartRepository;
import com.darwin.simplestore.repositories.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartService cartService;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OrderService orderService;


    private Cart cart;
    private Order order;
    private Product product1;
    private Product product2;

    @BeforeEach
    public void setUp() {
        product1 = new Product(
                1L,
                "p1",
                "d1",
                5.0,
                10L,
                ProductCategory.OTHER,
                null
        );
        product2 = new Product(
                2L,
                "p2",
                "d2",
                5.0,
                5L,
                ProductCategory.OTHER,
                null
        );
        cart = new Cart(1L,
                null
        );
        final CartItem cartItem1 = new CartItem(
                1L,
                10L,
                cart,
                product1
        );
        final CartItem cartItem2 = new CartItem(
                2L,
                4L,
                cart,
                product2
        );
        cart.setCartItems(Set.of(cartItem1, cartItem2));
        order = new Order(1L,
                cart,
                OrderStatus.AWAITING_PAYMENT);
    }

    @Test
    public void testAllProductsAvailable() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        assertTrue(orderService.allProductsAvailable(cart.getId()));

        product2.setQuantity(1L);

        assertFalse(orderService.allProductsAvailable(cart.getId()));

        verify(cartRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testAllProductsAvailableException() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> orderService.allProductsAvailable(cart.getId()));
    }

    @Test
    public void testPlaceOrder() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        doAnswer(i -> {
            final ProductDto productDto = i.getArgument(0);
            if (Objects.equals(productDto.id(), product1.getId())) {
                product1.setQuantity(productDto.quantity());
            } else {
                product2.setQuantity(productDto.quantity());
            }
            return null;
        }).when(productService).updateProductById(any(ProductDto.class));

        doAnswer(i -> {
            cart.setCartItems(Collections.emptySet());
            return null;
        }).when(cartService).clearCart(anyLong());

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(cartService.getCart(anyLong())).thenReturn(CartService.toCartDto(cart));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        assertDoesNotThrow(() -> orderService.placeOrder(cart.getId()));

        assertTrue(cart.getCartItems().isEmpty());
        assertEquals(0L, product1.getQuantity());
        assertEquals(1L, product2.getQuantity());

        verify(cartRepository, times(2)).findById(anyLong());
        verify(cartService, times(1)).getCart(anyLong());
        verify(cartService, times(1)).clearCart(anyLong());
        verify(productService, times(2)).updateProductById(any(ProductDto.class));
    }

    @Test
    public void testPlaceOrderException() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        product2.setQuantity(1L);

        assertThrowsExactly(NotEnoughProductsException.class, () -> orderService.placeOrder(cart.getId()));
    }

    @Test
    public void testGetOrder() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));

        final OrderDto orderDto = OrderService.toOrderDto(order);
        final OrderDto foundDto = orderService.getOrder(order.getId());

        assertEquals(orderDto, foundDto);
    }

    @Test
    public void testGetOrderException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> orderService.getOrder(order.getId()));

        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testPayOrder() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
           final Order thisOrder = i.getArgument(0);
           order.setStatus(thisOrder.getStatus());
           return thisOrder;
        });

        assertDoesNotThrow(() -> orderService.payOrder(order.getId()));
        assertEquals(OrderStatus.DELIVERED, order.getStatus());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testPayOrderException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> orderService.payOrder(order.getId()));
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testCancelOrder() {
        doAnswer(i -> {
            order = null;
            return null;
        }).when(orderRepository).deleteById(anyLong());

        when(orderRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> orderService.cancelOrder(order.getId()));
        assertNull(order);

        verify(orderRepository, times(1)).deleteById(anyLong());
        verify(orderRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testCancelOrderException() {
        when(orderRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> orderService.cancelOrder(order.getId()));

        verify(orderRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testFinishOrder() {
        order.setStatus(OrderStatus.DELIVERED);

        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        doAnswer(i -> {
            order = null;
            return null;
        }).when(orderRepository).delete(any(Order.class));

        assertDoesNotThrow(() -> orderService.finishOrder(order.getId()));
        assertNull(order);

        verify(orderRepository, times(1)).delete(any(Order.class));
        verify(orderRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testFinishOrderException() {
        when(orderRepository.findById(anyLong())).thenReturn(Optional.of(order));
        assertThrowsExactly(BadOrderStateException.class, () -> orderService.finishOrder(order.getId()));

        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrowsExactly(ResourceNotFoundException.class, () -> orderService.finishOrder(order.getId()));

        verify(orderRepository, times(2)).findById(anyLong());
    }
}
