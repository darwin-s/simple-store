package com.darwin.simplestore.repositories;

import com.darwin.simplestore.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
