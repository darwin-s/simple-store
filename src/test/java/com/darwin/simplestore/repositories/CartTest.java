// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.repositories;

import com.darwin.simplestore.TestcontainersConfiguration;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.entities.CartItem;
import com.darwin.simplestore.entities.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ActiveProfiles("dev")
public class CartTest {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

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

        productRepository.save(product1);
        productRepository.save(product2);
    }

    @AfterEach
    public void tearDown() {
        cartRepository.deleteAll();
        cartItemRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    public void testCart() {
        Cart cart = new Cart();

        Product product1 = productRepository.findByName("p1").get();
        Product product2 = productRepository.findByName("p2").get();

        CartItem cartItem1 = new CartItem();
        cartItem1.setCart(cart);
        cartItem1.setProduct(product1);
        cartItem1.setQuantity(2L);
        CartItem cartItem2 = new CartItem();
        cartItem2.setCart(cart);
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(3L);

        cart.setCartItems(Set.of(cartItem1, cartItem2));
        assertDoesNotThrow(() -> cartRepository.save(cart));

        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);
        assertEquals(2, cartItems.size());
    }

    @Test
    public void testDeleteCart() {
        Cart cart = new Cart();

        Product product1 = productRepository.findByName("p1").get();
        Product product2 = productRepository.findByName("p2").get();

        CartItem cartItem1 = new CartItem();
        cartItem1.setCart(cart);
        cartItem1.setProduct(product1);
        cartItem1.setQuantity(2L);
        CartItem cartItem2 = new CartItem();
        cartItem2.setCart(cart);
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(3L);

        cart.setCartItems(Set.of(cartItem1, cartItem2));
        cart = cartRepository.save(cart);

        cart.setCartItems(Collections.emptySet());
        cartRepository.save(cart);

        cartItemRepository.deleteAllByCartId(cart.getId());
        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

        assertEquals(0, cartItems.size());
    }
}
