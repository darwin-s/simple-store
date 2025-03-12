// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.NewProductDto;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.dto.ProductDto;
import com.darwin.simplestore.entities.Image;
import com.darwin.simplestore.entities.Product;
import com.darwin.simplestore.exceptions.ResourceExistsException;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.ImageRepository;
import com.darwin.simplestore.repositories.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class ProductServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    public void testCreateProduct() {
        final NewProductDto newProductDto = new NewProductDto(
                "tst",
                "tstDesc",
                1.0,
                3L,
                ProductCategory.OTHER
        );
        final Product product = ProductService.fromNewProductDto(newProductDto);
        product.setId(1L);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductDto productDto = assertDoesNotThrow(() -> productService.createProduct(newProductDto));

        assertEquals(1L, productDto.id());
        assertEquals("tst", productDto.name());
        assertEquals("tstDesc", productDto.description());
        assertEquals(1.0, productDto.price());
        assertEquals(3, productDto.quantity());
        assertEquals(ProductCategory.OTHER, productDto.category());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testCreateProductException() {
        final NewProductDto newProductDto = new NewProductDto(
                "tst",
                "tstDesc",
                1.0,
                3L,
                ProductCategory.OTHER
        );

        when(productRepository.existsByName(anyString())).thenReturn(true);

        assertThrowsExactly(ResourceExistsException.class, () -> productService.createProduct(newProductDto));

        verify(productRepository, times(1)).existsByName(anyString());
    }

    @Test
    public void testGetAllProducts() {
        final List<Product> products = List.of(
                new Product(
                        1L,
                        "p1",
                        "d1",
                        1.0,
                        2L,
                        ProductCategory.OTHER,
                        null
                ),
                new Product(
                        2L,
                        "p2",
                        "d2",
                        1.0,
                        2L,
                        ProductCategory.OTHER,
                        null
                )
        );

        when(productRepository.findAll()).thenReturn(products);

        final List<ProductDto> expectedProducts = products.stream().map(ProductService::toProductDto).toList();
        final List<ProductDto> productDtos = productService.getAllProducts();

        assertEquals(expectedProducts, productDtos);

        verify(productRepository, times(1)).findAll();
    }

    @Test
    public void testGetProducts() {
        final Product p1 = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                2L,
                ProductCategory.OTHER,
                null);
        final Product p2 = new Product(
                2L,
                "p2",
                "d2",
                1.0,
                2L,
                ProductCategory.OTHER,
                null);

        final Pageable pageable1 = PageRequest.of(0, 1);
        final Pageable pageable2 = PageRequest.of(1, 1);
        final Page<Product> p1Page = new PageImpl<>(List.of(p1), pageable1, 1);
        final Page<Product> p2Page = new PageImpl<>(List.of(p2), pageable2, 1);

        when(productRepository.findAll(pageable1)).thenReturn(p1Page);
        when(productRepository.findAll(pageable2)).thenReturn(p2Page);

        Page<ProductDto> firstPage = productService.getProducts(pageable1);

        assertEquals(1, firstPage.getNumberOfElements());
        assertEquals(1L, firstPage.get().findFirst().get().id());

        Page<ProductDto> secondPage = productService.getProducts(pageable2);

        assertEquals(1, secondPage.getNumberOfElements());
        assertEquals(2L, secondPage.get().findFirst().get().id());

        verify(productRepository, times(2)).findAll(any(Pageable.class));
    }

    @Test
    public void testGetProductById() {
        final Product product = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                2L,
                ProductCategory.OTHER,
                null);

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        ProductDto productDto = assertDoesNotThrow(() -> productService.getProductById(1L));
        assertEquals(1L, productDto.id());

        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetProductByIdException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.getProductById(1L));

        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetProductByName() {
        final Product product = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                2L,
                ProductCategory.OTHER,
                null);

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(product));

        ProductDto productDto = assertDoesNotThrow(() -> productService.getProductByName("p1"));
        assertEquals(1L, productDto.id());

        verify(productRepository, times(1)).findByName(anyString());
    }

    @Test
    public void testGetProductByNameException() {
        when(productRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.getProductByName("p1"));

        verify(productRepository, times(1)).findByName(anyString());
    }

    @Test
    public void testGetProductByCategory() {
        final Product p1 = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                2L,
                ProductCategory.OTHER,
                null);
        final Product p2 = new Product(
                2L,
                "p2",
                "d2",
                1.0,
                2L,
                ProductCategory.OTHER,
                null);

        final Pageable pageable1 = PageRequest.of(0, 1);
        final Pageable pageable2 = PageRequest.of(1, 1);
        final Page<Product> p1Page = new PageImpl<>(List.of(p1), pageable1, 1);
        final Page<Product> p2Page = new PageImpl<>(List.of(p2), pageable2, 1);
        final Page<Product> emptyPage = new PageImpl<>(List.of(), pageable1, 0);

        when(productRepository.findByCategory(pageable1, ProductCategory.OTHER)).thenReturn(p1Page);
        when(productRepository.findByCategory(pageable2, ProductCategory.OTHER)).thenReturn(p2Page);
        when(productRepository.findByCategory(any(Pageable.class), eq(ProductCategory.CLOTHES))).thenReturn(emptyPage);

        Page<ProductDto> firstPage = productService.getProductsByCategory(pageable1, ProductCategory.OTHER);

        assertEquals(1, firstPage.getNumberOfElements());
        assertEquals(1L, firstPage.get().findFirst().get().id());

        Page<ProductDto> secondPage = productService.getProductsByCategory(pageable2, ProductCategory.OTHER);

        assertEquals(1, secondPage.getNumberOfElements());
        assertEquals(2L, secondPage.get().findFirst().get().id());

        Page<ProductDto> thirdPage = productService.getProductsByCategory(pageable1, ProductCategory.CLOTHES);
        assertEquals(0, thirdPage.getNumberOfElements());

        verify(productRepository, times(3)).findByCategory(any(Pageable.class), any(ProductCategory.class));
    }

    @Test
    public void testUpdateProductById() {
        final Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);

        when(productRepository.existsById(anyLong())).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertDoesNotThrow(() -> productService.updateProductById(ProductService.toProductDto(product)));

        verify(product, times(1)).getId();
        verify(productRepository, times(1)).existsById(anyLong());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProductByIdException() {
        final Product product = mock(Product.class);
        when(product.getId()).thenReturn(1L);

        when(productRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.updateProductById(ProductService.toProductDto(product)));

        verify(product, times(1)).getId();
        verify(productRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testUpdateProductByName() {
        final Product product = mock(Product.class);
        when(product.getName()).thenReturn("p1");

        when(productRepository.findByName(anyString())).thenReturn(Optional.of(product));
        when(productRepository.existsByName(anyString())).thenReturn(true);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        assertDoesNotThrow(() -> productService.updateProductByName(ProductService.toProductDto(product)));

        verify(product, times(1)).getName();
        verify(productRepository, times(1)).existsByName(anyString());
        verify(productRepository, times(1)).existsByName(anyString());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    public void testUpdateProductByNameException() {
        final Product product = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                2L,
                ProductCategory.OTHER,
                null);

        when(productRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.updateProductById(ProductService.toProductDto(product)));

        verify(productRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testDeleteProductById() {
        when(productRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProductById(1L));

        verify(productRepository, times(1)).existsById(anyLong());
        verify(productRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteProductByIdException() {
        when(productRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.deleteProductById(1L));

        verify(productRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testDeleteProductByName() {
        when(productRepository.existsByName(anyString())).thenReturn(true);

        assertDoesNotThrow(() -> productService.deleteProductByName("p1"));

        verify(productRepository, times(1)).existsByName(anyString());
        verify(productRepository, times(1)).deleteByName(anyString());
    }

    @Test
    public void testDeleteProductByNameException() {
        when(productRepository.existsByName(anyString())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.deleteProductByName("p1"));

        verify(productRepository, times(1)).existsByName(anyString());
    }

    @Test
    public void testSetImage() {
        final Image image = new Image(
                1L,
                "base64"
        );

        final Product product = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                1L,
                ProductCategory.OTHER,
                null
        );

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));
        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        assertDoesNotThrow(() -> productService.setImage(1L, 1L));
        assertEquals(image, product.getImage());

        verify(imageRepository, times(1)).findById(anyLong());
        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testSetImageException() {
        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.setImage(1L, 1L));

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.setImage(1L, 1L));

        verify(productRepository, times(2)).findById(anyLong());
    }

    @Test
    public void testGetImage() {
        final Image image = new Image(
                1L,
                "base64"
        );

        final Product product = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                1L,
                ProductCategory.OTHER,
                null
        );

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertFalse(productService.getImage(1L).isPresent());

        product.setImage(image);

        assertTrue(productService.getImage(1L).isPresent());
        assertEquals(ImageService.toImageDto(image), productService.getImage(1L).get());
    }

    @Test
    public void testGetImageException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.getImage(1L));

        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testRemoveImage() {
        final Image image = new Image(
                1L,
                "base64"
        );

        final Product product = new Product(
                1L,
                "p1",
                "d1",
                1.0,
                1L,
                ProductCategory.OTHER,
                null
        );

        when(productRepository.findById(anyLong())).thenReturn(Optional.of(product));

        assertDoesNotThrow(() -> productService.removeImage(1L));
        assertNull(product.getImage());

        verify(productRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testRemoveImageException() {
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.removeImage(1L));

        verify(productRepository, times(1)).findById(anyLong());
    }
}
