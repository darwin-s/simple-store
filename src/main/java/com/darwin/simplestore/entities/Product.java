// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.entities;

import com.darwin.simplestore.dto.ProductCategory;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @NotNull(message = "Product name cannot be null")
    @NotEmpty(message = "Product name cannot be empty")
    private String name;

    @Size(max = 4096, message = "Product description can be at most 4096 characters long")
    @Column(length = 4096)
    private String description;

    @NotNull(message = "Product price cannot be null")
    private Double price;

    @NotNull(message = "Product quantity cannot be null")
    private Long quantity;

    @Enumerated(EnumType.STRING)
    private ProductCategory category;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id", referencedColumnName = "id", nullable = true)
    private Image image;
}
