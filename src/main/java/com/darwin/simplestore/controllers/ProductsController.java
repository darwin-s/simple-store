package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewProductDto;
import com.darwin.simplestore.dto.ProductDto;
import com.darwin.simplestore.exceptions.ResourceExistsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    @GetMapping
    public ResponseEntity<Page<ProductDto>> getProductsPage(
        @RequestParam(defaultValue = "0") final Integer page,
        @RequestParam(defaultValue = "5") final Integer pageSize,
        @RequestParam(defaultValue = "name") final String sortBy,
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
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody final NewProductDto newProductDto) throws ResourceExistsException {
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
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable final Long productId) throws ResourceNotFoundException {
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
    @PutMapping("/{productId}")
    public ResponseEntity<Void> updateProduct(@PathVariable final Long productId, @RequestBody final NewProductDto productDto) throws ResourceNotFoundException {
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
    public ResponseEntity<Void> deleteProduct(@PathVariable final Long productId) throws ResourceNotFoundException {
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
    public ResponseEntity<Void> setImage(@PathVariable final Long productId, @RequestParam final Long imageId) throws ResourceNotFoundException {
        productService.setImage(productId, imageId);

        return ResponseEntity.ok().build();
    }

    /**
     * Get the image for a product
     * @param productId The id of the product
     * @return The image DTO associated with the product or 404
     * @throws ResourceNotFoundException If the product could not be found
     */
    @GetMapping("/{productId}/image")
    public ResponseEntity<ImageDto> getImage(@PathVariable final Long productId) throws ResourceNotFoundException {
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
    public ResponseEntity<Void> deleteImage(@PathVariable final Long productId) throws ResourceNotFoundException {
        productService.removeImage(productId);

        return ResponseEntity.ok().build();
    }
}
