package com.darwin.simplestore.repositories;

import com.darwin.simplestore.entities.Cart;
import com.darwin.simplestore.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByCart(Cart cart);
    void deleteAllByCartId(Long cartId);
}
