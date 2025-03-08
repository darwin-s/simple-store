package com.darwin.simplestore.repositories;

import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.entities.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@ActiveProfiles("dev")
class ProductTest {
    @Autowired
    private ProductRepository productRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:latest");

    @BeforeEach
    public void setUp() {
        Product product = new Product();
        product.setName("unique");
        product.setDescription("uniqueDesc");
        product.setPrice(1.0);
        product.setQuantity(10);
        product.setCategory(ProductCategory.OTHER);

        productRepository.save(product);
    }

    @AfterEach
    public void tearDown() {
        productRepository.deleteAll();
    }

    @Test
    public void testSave() {
        Product product = new Product();
        product.setName("test");
        product.setDescription("desc");
        product.setPrice(1.0);
        product.setQuantity(10);
        product.setCategory(ProductCategory.OTHER);

        assertDoesNotThrow(() -> productRepository.save(product));
    }

    @Test
    public void testFindByName() {
        Optional<Product> productOptional = productRepository.findByName("unique");

        assertTrue(productOptional.isPresent());
        Product product = productOptional.get();

        assertEquals("uniqueDesc", product.getDescription());
    }

    @Test
    public void testFindByCategory() {
        Product product = new Product();
        product.setName("test");
        product.setDescription("desc");
        product.setPrice(1.0);
        product.setQuantity(10);
        product.setCategory(ProductCategory.OTHER);
        productRepository.save(product);

        List<Product> products = productRepository.findByCategory(ProductCategory.OTHER);
        assertEquals(2, products.size());
        assertEquals("unique", products.getFirst().getName());
        assertEquals("test", products.getLast().getName());
    }
}
