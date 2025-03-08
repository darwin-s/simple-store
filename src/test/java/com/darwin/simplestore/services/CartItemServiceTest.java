package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.CartItemDto;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.entities.CartItem;
import com.darwin.simplestore.entities.Product;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.CartItemRepository;
import com.darwin.simplestore.repositories.CartRepository;
import com.darwin.simplestore.repositories.ProductRepository;
import org.apache.commons.compress.utils.Sets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class CartItemServiceTest {
    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService cartItemService;

    private static Product product;
    private static Cart emptyCart;
    private static Cart cart;
    private static CartItem cartItem;

    @BeforeEach
    public void setUp() {
        product = new Product(
                1L,
                "tst",
                "tstDesc",
                1.0,
                5L,
                ProductCategory.OTHER
        );
        emptyCart = new Cart(
                1L,
                new HashSet<>()
        );
        cart = new Cart(2L, Collections.emptySet());
        cartItem = new CartItem(
                1L,
                5L,
                cart,
                product);

        cart.setCartItems(Sets.newHashSet(cartItem));

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(emptyCart));
    }

    @Test
    public void testAddProductToEmptyCart() {
        assertDoesNotThrow(() -> cartItemService.addProductToCart(0L, 0L, 5L));

        assertFalse(emptyCart.getCartItems().isEmpty());
        assertEquals(5L, emptyCart.getCartItems().stream().findFirst().get().getQuantity());

        verify(cartRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testAddProductToCart() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        assertDoesNotThrow(() -> cartItemService.addProductToCart(0L, 0L, 5L));
        assertEquals(10L, cart.getCartItems().stream().findFirst().get().getQuantity());

        verify(cartRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testAddProductToCartException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.addProductToCart(0L, 0L, 5L));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.addProductToCart(0L, 0L, 5L));

        verify(cartRepository, times(2)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetCartItem() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        final CartItemDto cartItemDto = assertDoesNotThrow(() -> cartItemService.getCartItem(0L, 0L));
        assertEquals(CartItemService.toCartItemDto(cartItem), cartItemDto);

        verify(cartRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetCartItemException() {
        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.getCartItem(0L, 0L));

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.getCartItem(0L, 0L));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.getCartItem(0L, 0L));

        verify(cartRepository, times(3)).findById(anyLong());
        verify(productRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testUpdateItemQuantity() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        assertDoesNotThrow(() -> cartItemService.updateItemQuantity(0L, 0L, 15L));
        assertEquals(15L, cart.getCartItems().stream().findFirst().get().getQuantity());

        verify(cartRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    public void testUpdateItemQuantityException() {
        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.updateItemQuantity(0L, 0L, 15L));

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.updateItemQuantity(0L, 0L, 15L));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.updateItemQuantity(0L, 0L, 15L));

        verify(cartRepository, times(3)).findById(anyLong());
        verify(productRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testDeleteItem() {
        when(cartRepository.findById(anyLong())).thenReturn(Optional.of(cart));

        assertDoesNotThrow(() -> cartItemService.deleteItem(0L, 0L));
        assertTrue(cart.getCartItems().isEmpty());

        verify(cartRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
        verify(cartItemRepository, times(1)).deleteById(anyLong());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }

    @Test
    public void testDeleteItemException() {
        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.deleteItem(0L, 0L));

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.deleteItem(0L, 0L));
        when(cartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> cartItemService.deleteItem(0L, 0L));

        verify(cartRepository, times(3)).findById(anyLong());
        verify(productRepository, times(2)).findById(anyLong());
    }
}
