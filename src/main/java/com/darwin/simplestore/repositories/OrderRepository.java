package com.darwin.simplestore.repositories;

import com.darwin.simplestore.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCartId(Long cartId);
}
