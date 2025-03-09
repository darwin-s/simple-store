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
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(length = 4096)
    private String description;
    private Double price;
    private Long quantity;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id", nullable = true)
    private Image image;
}
