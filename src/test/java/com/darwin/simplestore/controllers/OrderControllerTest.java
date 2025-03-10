// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.*;
import com.darwin.simplestore.services.OrderService;
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

import java.util.Set;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@WebMvcTest(controllers = OrderController.class)
public class OrderControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    private CartDto cartDto;
    private OrderDto awaitingOrderDto;
    private OrderDto deliveredOrderDto;

    @BeforeEach
    public void setUp() {
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

        cartDto = new CartDto(
                2L,
                Set.of(cartItemDto1, cartItemDto2)
        );

        awaitingOrderDto = new OrderDto(
                1L,
                cartDto,
                OrderStatus.AWAITING_PAYMENT
        );

        deliveredOrderDto = new OrderDto(
                1L,
                cartDto,
                OrderStatus.DELIVERED
        );
    }

    @Test
    public void testPlaceOrder() throws Exception {
        when(orderService.placeOrder(anyLong())).thenReturn(awaitingOrderDto);

        mvc.perform(post("/orders")
                .param("cartId", "1"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/orders/1")))
                .andExpect(content().string(objectMapper.writeValueAsString(awaitingOrderDto)));
    }

    @Test
    public void testGetOrder() throws Exception {
        when(orderService.getOrder(anyLong())).thenReturn(deliveredOrderDto);

        mvc.perform(get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(deliveredOrderDto)));
    }

    @Test
    public void testPayOrder() throws Exception {
        mvc.perform(post("/orders/1/pay"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCancelOrder() throws Exception {
        mvc.perform(delete("/orders/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testFinishOrder() throws Exception {
        mvc.perform(post("/orders/1/finish"))
                .andExpect(status().isOk());
    }
}
