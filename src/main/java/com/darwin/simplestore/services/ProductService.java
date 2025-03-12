// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewProductDto;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.dto.ProductDto;
import com.darwin.simplestore.entities.Image;
import com.darwin.simplestore.entities.Product;
import com.darwin.simplestore.exceptions.ResourceExistsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.ImageRepository;
import com.darwin.simplestore.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

/**
 * Service class used for managing products in the repository
 */
@Service
@RequiredArgsConstructor
@Validated
public class ProductService {
    private final ProductRepository productRepository;
    private final ImageRepository imageRepository;

    /**
     * Create a new product
     * @param newProductDto The DTO representing the new product to be added
     * @return DTO representing the created product
     * @throws ResourceExistsException If the product with the same name already exists
     */
    public ProductDto createProduct(final NewProductDto newProductDto) throws ResourceExistsException {
        if (productRepository.existsByName(newProductDto.name())) {
            throw new ResourceExistsException("Product with name " + newProductDto.name() + " already exists");
        }

        Product product = fromNewProductDto(newProductDto);

        return toProductDto(productRepository.save(product));
    }

    /**
     * Get all the products.
     * This method is very inefficient when there are a lot of resources.
     * The page method should be preferred to this one
     * @return A list of all the products in the repository
     * @see #getProducts(Pageable) 
     */
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll().stream().map(ProductService::toProductDto).toList();
    }

    /**
     * Get a page of products
     * @param pageable The pageable object, holding the pagination parameters
     * @return A page of products
     */
    public Page<ProductDto> getProducts(final Pageable pageable) {
        return productRepository.findAll(pageable).map(ProductService::toProductDto);
    }

    /**
     * Get a product by its id
     * @param id The id of the product
     * @return Product DTO representing the requested product
     * @throws ResourceNotFoundException If no product exists with the requested id
     */
    public ProductDto getProductById(final Long id) throws ResourceNotFoundException {
        return toProductDto(productRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found")));
    }

    /**
     * Get a product by its name
     * @param name The name of the product
     * @return Product DTO representing the requested product
     * @throws ResourceNotFoundException If no product exists with the requested name
     */
    public ProductDto getProductByName(final String name) throws ResourceNotFoundException {
        return toProductDto(productRepository.findByName(name).orElseThrow(() -> new ResourceNotFoundException("Product with name " + name + " not found")));
    }

    /**
     * Get products by category
     * @param pageable The pageable for the request
     * @param category The product category
     * @return Page of products of certain category
     */
    public Page<ProductDto> getProductsByCategory(final Pageable pageable, final ProductCategory category) {
        return productRepository.findByCategory(pageable, category).map(ProductService::toProductDto);
    }

    /**
     * Update an existing product in the repository using its id inside the DTO
     * @param productDto The product DTO to update
     * @throws ResourceNotFoundException If the product with the specified id inside the DTO does not exist
     */
    public void updateProductById(final ProductDto productDto) throws ResourceNotFoundException {
        if (!productRepository.existsById(productDto.id())) {
            throw new ResourceNotFoundException("Product with id " + productDto.id() + " does not exist");
        }

        Product product = fromProductDto(productDto);
        productRepository.save(product);
    }

    /**
     * Update an existing product in the repository using its name inside the DTO
     * @param productDto The product DTO to update
     * @throws ResourceNotFoundException If the product with the specified name inside the DTO does not exist
     */
    public void updateProductByName(final ProductDto productDto) throws ResourceNotFoundException {
        if (!productRepository.existsByName(productDto.name())) {
            throw new ResourceNotFoundException("Product with name " + productDto.name() + " does not exist");
        }

        Product product = fromProductDto(productDto);
        product.setId(productRepository.findByName(productDto.name()).get().getId());
        productRepository.save(product);
    }

    /**
     * Delete a product by its id
     * @param id The id of the product
     * @throws ResourceNotFoundException If no product exists with the specified id
     */
    public void deleteProductById(final Long id) throws ResourceNotFoundException {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product with id " + id + " does not exist");
        }

        productRepository.deleteById(id);
    }

    /**
     * Delete a product by its name
     * @param name The name of the product
     * @throws ResourceNotFoundException If no product exists with the specified name
     */
    public void deleteProductByName(final String name) throws ResourceNotFoundException {
        if (!productRepository.existsByName(name)) {
            throw new ResourceNotFoundException("Product with name " + name + " does not exist");
        }

        productRepository.deleteByName(name);
    }

    /**
     * Set an image for the product
     * @param productId The id of the product
     * @param imageId The id of the image
     * @throws ResourceNotFoundException If the product or the image could not be found
     */
    public void setImage(final Long productId, final Long imageId) throws ResourceNotFoundException {
        final Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " does not exist"));
        final Image image = imageRepository.findById(imageId).orElseThrow(() -> new ResourceNotFoundException("Image with id " + imageId + " does not exist"));

        product.setImage(image);
        productRepository.save(product);
    }

    /**
     * Get the image of the product
     * @param productId The id of the product
     * @return The image DTO
     * @throws ResourceNotFoundException If the product could not be found
     */
    public Optional<ImageDto> getImage(final Long productId) throws ResourceNotFoundException {
        final Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " does not exist"));

        if (product.getImage() == null) {
            return Optional.empty();
        } else {
            return Optional.of(ImageService.toImageDto(product.getImage()));
        }
    }

    /**
     * Remove the image from the product
     * @param productId the id of the product
     * @throws ResourceNotFoundException If the product could not be found
     */
    public void removeImage(final Long productId) throws ResourceNotFoundException {
        final Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product with id " + productId + " does not exist"));

        product.setImage(null);
        productRepository.save(product);
    }

    /**
     * Convert a new product DTO to an entity
     * @param newProductDto The new product DTO
     * @return A product entity, without its id set
     */
    public static Product fromNewProductDto(final NewProductDto newProductDto) {
        Product product = new Product();
        product.setName(newProductDto.name());
        product.setDescription(newProductDto.description());
        product.setPrice(newProductDto.price());
        product.setQuantity(newProductDto.quantity());
        product.setCategory(newProductDto.category());

        return product;
    }

    /**
     * Convert a product DTO to an entity
     * @param productDto The product DTO
     * @return A product entity corresponding to the DTO
     */
    public static Product fromProductDto(final ProductDto productDto) {
        return new Product(productDto.id(),
                productDto.name(),
                productDto.description(),
                productDto.price(),
                productDto.quantity(),
                productDto.category(),
                null);
    }

    /**
     * Convert a product entity to a DTO
     * @param product The product entity
     * @return A DTO corresponding to the entity
     */
    public static ProductDto toProductDto(final Product product) {
        return new ProductDto(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategory());
    }
}
