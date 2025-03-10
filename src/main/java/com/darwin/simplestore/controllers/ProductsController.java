// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewProductDto;
import com.darwin.simplestore.dto.ProductDto;
import com.darwin.simplestore.exceptions.ResourceExistsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.Optional;

/**
 * Controller for managing products
 */
@RestController
@RequestMapping("products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Endpoints for managing products")
public class ProductsController {
    private final ProductService productService;

    /**
     * Get a page of products
     * @param page The page number
     * @param pageSize The size of the page
     * @param sortBy Field inside the product object to sort by
     * @param ascending If true sorts ascending, otherwise sorts descending
     * @return A page of product DTOs
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get products page", description = "Get a page of products")
    public ResponseEntity<Page<ProductDto>> getProductsPage(
        @Parameter(description = "The page number to return", example = "0")
        @RequestParam(defaultValue = "0") final Integer page,
        @Parameter(description = "The number of products per page", example = "5")
        @RequestParam(defaultValue = "5") final Integer pageSize,
        @Parameter(description = "The field to sort the products by", example = "name")
        @RequestParam(defaultValue = "name") final String sortBy,
        @Parameter(description = "Whether to sort the page in ascending order", example = "true")
        @RequestParam(defaultValue = "true") final Boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by("name").descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);

        return ResponseEntity.ok(productService.getProducts(pageable));
    }

    /**
     * Create a new product
     * @param newProductDto DTO representing the product to be created
     * @return DTO representing the created product
     * @throws ResourceExistsException If the product already existed in the database
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create product", description = "Create a new product and add it to the database")
    public ResponseEntity<ProductDto> createProduct(
            @Parameter(description = "The description of the new product")
            @RequestBody final NewProductDto newProductDto) throws ResourceExistsException {

        final ProductDto product = productService.createProduct(newProductDto);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(product.id())
                .toUri();

        return ResponseEntity.created(location).body(product);
    }

    /**
     * Return a product by its id
     * @param productId The id of the product
     * @return The DTO of the requested product
     * @throws ResourceNotFoundException If the specified product could not be found
     */
    @GetMapping(value = "/{productId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get product", description = "Retrieve and return a product from the database")
    public ResponseEntity<ProductDto> getProduct(
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId) throws ResourceNotFoundException {

        final ProductDto product = productService.getProductById(productId);

        return ResponseEntity.ok(product);
    }

    /**
     * Update a product
     * @param productId The id of the product
     * @param productDto The DTO of the product
     * @return Response object
     * @throws ResourceNotFoundException If the requested product does not exist
     */
    @PutMapping(value = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update product", description = "Update an existing product")
    public ResponseEntity<Void> updateProduct(
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId,
            @Parameter(description = "The new description of the product")
            @RequestBody final NewProductDto productDto) throws ResourceNotFoundException {

        final ProductDto product = productService.getProductById(productId);
        final ProductDto updatedProduct = new ProductDto(
                product.id(),
                productDto.name(),
                productDto.description(),
                productDto.price(),
                productDto.quantity(),
                productDto.category()
        );

        productService.updateProductById(updatedProduct);

        return ResponseEntity.ok().build();
    }

    /**
     * Delete a product
     * @param productId the id of the product
     * @return Response object
     * @throws ResourceNotFoundException If the requested product does not exist
     */
    @DeleteMapping("/{productId}")
    @Operation(summary = "Delete product", description = "Delete an existing product")
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId) throws ResourceNotFoundException {

        productService.deleteProductById(productId);

        return ResponseEntity.ok().build();
    }

    /**
     * Set an image for the product
     * @param productId The id of the product
     * @param imageId The id of the image
     * @return Response object
     * @throws ResourceNotFoundException If the product or the image could not be found
     */
    @PutMapping("/{productId}/image")
    @Operation(summary = "Set image", description = "Set an image for the product, overriding the previous one if any")
    public ResponseEntity<Void> setImage(
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId,
            @Parameter(description = "The id of the image", example = "1")
            @RequestParam final Long imageId) throws ResourceNotFoundException {

        productService.setImage(productId, imageId);

        return ResponseEntity.ok().build();
    }

    /**
     * Get the image for a product
     * @param productId The id of the product
     * @return The image DTO associated with the product or 404
     * @throws ResourceNotFoundException If the product could not be found
     */
    @GetMapping(value = "/{productId}/image", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get image", description = "Get the image associated with the product")
    public ResponseEntity<ImageDto> getImage(
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId) throws ResourceNotFoundException {

        final Optional<ImageDto> imageDto = productService.getImage(productId);

        return imageDto.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Remove the image from a product
     * @param productId The id of the product
     * @return Response object
     * @throws ResourceNotFoundException If the product could not be found
     */
    @DeleteMapping("/{productId}/image")
    @Operation(summary = "Delete image", description = "Remove the image from the product")
    public ResponseEntity<Void> deleteImage(
            @Parameter(description = "The id of the product", example = "1")
            @PathVariable final Long productId) throws ResourceNotFoundException {

        productService.removeImage(productId);

        return ResponseEntity.ok().build();
    }
}
