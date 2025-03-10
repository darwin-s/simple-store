// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.repositories;

import com.darwin.simplestore.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository for cart objects
 */
public interface CartRepository extends JpaRepository<Cart, Long> {

}
