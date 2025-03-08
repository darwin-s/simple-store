package com.darwin.simplestore.entities;

import com.darwin.simplestore.dto.ProductCategory;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class, representing a product in the store
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String description;
    private Double price;
    private Integer quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;
}
