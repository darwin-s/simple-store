package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.CartDto;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.CartItemRepository;
import com.darwin.simplestore.repositories.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class CartServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartService cartService;

    @Test
    public void testCreateCart() {
        Cart cart = new Cart(1L, Collections.emptySet());

        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        final CartDto cartDto = assertDoesNotThrow(() -> cartService.createCart());
        assertEquals(CartService.toCartDto(cart), cartDto);

        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testGetCart() {
        Cart cart = new Cart(1L, Collections.emptySet());

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        final CartDto cartDto = assertDoesNotThrow(() -> cartService.getCart(1L));
        assertEquals(CartService.toCartDto(cart), cartDto);

        verify(cartRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetCartException() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartService.getCart(1L));

        verify(cartRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testClearCart() {
        Cart cart = new Cart(1L, Collections.emptySet());

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(cartRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> cartService.clearCart(1L));

        verify(cartRepository, times(1)).existsById(anyLong());
        verify(cartItemRepository, times(1)).deleteAllByCartId(anyLong());
    }

    @Test
    public void testClearCartException() {
        when(cartRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartService.clearCart(1L));

        verify(cartRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testDeleteCart() {
        Cart cart = new Cart(1L, Collections.emptySet());

        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));
        when(cartRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> cartService.deleteCart(1L));

        verify(cartRepository, times(1)).existsById(anyLong());
        verify(cartItemRepository, times(1)).deleteAllByCartId(anyLong());
    }

    @Test
    public void testDeleteCartException() {
        when(cartRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartService.deleteCart(1L));

        verify(cartRepository, times(1)).existsById(anyLong());
    }
}
