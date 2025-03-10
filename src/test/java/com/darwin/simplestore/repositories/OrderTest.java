// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.repositories;

import com.darwin.simplestore.TestcontainersConfiguration;
import com.darwin.simplestore.dto.OrderStatus;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.entities.CartItem;
import com.darwin.simplestore.entities.Order;
import com.darwin.simplestore.entities.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ActiveProfiles("dev")
public class OrderTest {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    private Cart cart;

    @BeforeEach
    public void setUp() {
        Product product1 = new Product();
        product1.setName("p1");
        product1.setDescription("d1");
        product1.setPrice(1.0);
        product1.setQuantity(10L);
        product1.setCategory(ProductCategory.OTHER);

        Product product2 = new Product();
        product2.setName("p2");
        product2.setDescription("d2");
        product2.setPrice(1.0);
        product2.setQuantity(10L);
        product2.setCategory(ProductCategory.FOOD);

        product1 = productRepository.save(product1);
        product2 = productRepository.save(product2);

        CartItem cartItem1 = new CartItem();
        cartItem1.setProduct(product1);
        cartItem1.setQuantity(10L);

        CartItem cartItem2 = new CartItem();
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(5L);

        cart = new Cart();
        cart.setCartItems(Set.of(cartItem1, cartItem2));
        cartItem1.setCart(cart);
        cartItem2.setCart(cart);

        cart = cartRepository.save(cart);
    }

    @AfterEach
    public void tearDown() {
        orderRepository.deleteAll();
        cartItemRepository.deleteAll();
        cartRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    public void testSaveOrder() {
        Order order = new Order();
        order.setCart(cart);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);

        order = orderRepository.save(order);
        final Optional<Order> foundOrder = orderRepository.findByCartId(cart.getId());

        assertTrue(foundOrder.isPresent());
        assertEquals(order.getId(), foundOrder.get().getId());
    }
}
