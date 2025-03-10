// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.repositories;

import com.darwin.simplestore.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for order objects
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByCartId(Long cartId);
}
