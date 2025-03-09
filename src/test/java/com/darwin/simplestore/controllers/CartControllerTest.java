package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.CartDto;
import com.darwin.simplestore.dto.CartItemDto;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.dto.ProductDto;
import com.darwin.simplestore.services.CartItemService;
import com.darwin.simplestore.services.CartService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@WebMvcTest(controllers = CartController.class)
public class CartControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private CartItemService cartItemService;

    private CartDto emptyCart;
    private CartDto cart;

    @BeforeEach
    public void setUp() {
        emptyCart = new CartDto(
                1L,
                Collections.emptySet()
        );

        final ProductDto productDto1 = new ProductDto(
                1L,
                "p1",
                "d1",
                10.0,
                4L,
                ProductCategory.OTHER
        );

        final ProductDto productDto2 = new ProductDto(
                2L,
                "p2",
                "d2",
                10.0,
                4L,
                ProductCategory.OTHER
        );

        final CartItemDto cartItemDto1 = new CartItemDto(
                1L,
                productDto1,
                1L
        );

        final CartItemDto cartItemDto2 = new CartItemDto(
                2L,
                productDto2,
                1L
        );

        cart = new CartDto(
                2L,
                Set.of(cartItemDto1, cartItemDto2)
        );
    }

    @Test
    public void testCreateCart() throws Exception {
        when(cartService.createCart()).thenReturn(emptyCart);

        mvc.perform(post("/carts"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/carts/1")))
                .andExpect(content().string(objectMapper.writeValueAsString(emptyCart)));
    }

    @Test
    public void testAddCartItem() throws Exception {
        mvc.perform(post("/carts/1/add")
                .param("cartId", "1")
                .param("productId", "1")
                .param("quantity", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetCart() throws Exception {
        when(cartService.getCart(anyLong())).thenReturn(cart);

        mvc.perform(get("/carts/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(cart)));
    }

    @Test
    public void testGetCartItem() throws Exception {
        final CartItemDto cartItemDto = cart.cartItems().stream().findFirst().get();

        when(cartItemService.getCartItem(anyLong(), anyLong())).thenReturn(cartItemDto);

        mvc.perform(get("/carts/1/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(cartItemDto)));
    }

    @Test
    public void testClearCart() throws Exception {
        mvc.perform(post("/carts/1/clear"))
                .andExpect(status().isOk());
    }

    @Test
    public void testUpdateQuantity() throws Exception {
        mvc.perform(put("/carts/1/1")
                .param("quantity", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteCart() throws Exception {
        mvc.perform(delete("/carts/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteItem() throws Exception {
        mvc.perform(delete("/carts/1/1"))
                .andExpect(status().isOk());
    }
}
