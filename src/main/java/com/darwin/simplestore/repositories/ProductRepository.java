package com.darwin.simplestore.repositories;

import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for product objects
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByName(String name);
    List<Product> findByCategory(ProductCategory category);
    boolean existsByName(String name);
    void deleteByName(String name);
}
