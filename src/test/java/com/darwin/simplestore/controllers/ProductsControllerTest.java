// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.controllers;

import com.darwin.simplestore.config.DataWebConfig;
import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewProductDto;
import com.darwin.simplestore.dto.ProductCategory;
import com.darwin.simplestore.dto.ProductDto;
import com.darwin.simplestore.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("dev")
@WebMvcTest(controllers = ProductsController.class)
@Import(DataWebConfig.class)
public class ProductsControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDto productDto;
    private NewProductDto newProductDto;

    @BeforeEach
    public void setUp() {
        productDto = new ProductDto(
                1L,
                "p1",
                "d1",
                10.0,
                4L,
                ProductCategory.OTHER
        );

        newProductDto = new NewProductDto(
                "p1",
                "d1",
                10.0,
                4L,
                ProductCategory.OTHER
        );
    }

    @Test
    public void testGetProductsPage() throws Exception {
        when(productService.getProducts(any(Pageable.class))).thenReturn(new PageImpl<>(List.of(productDto)));

        mvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L));
    }

    @Test
    public void testGetProductsPageByCategory() throws Exception {
        when(productService.getProductsByCategory(any(Pageable.class), eq(ProductCategory.OTHER))).thenReturn(new PageImpl<>(List.of(productDto)));
        when(productService.getProductsByCategory(any(Pageable.class), eq(ProductCategory.CLOTHES))).thenReturn(new PageImpl<>(List.of()));

        mvc.perform(get("/products")
                .param("category", ProductCategory.OTHER.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1L));

        mvc.perform(get("/products")
                .param("category", ProductCategory.CLOTHES.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page.totalElements").value(0));
    }

    @Test
    public void testCreateProduct() throws Exception {
        when(productService.createProduct(any(NewProductDto.class))).thenAnswer(i -> {
           final NewProductDto productDto = i.getArgument(0);
           return new ProductDto(
                   1L,
                   productDto.name(),
                   productDto.description(),
                   productDto.price(),
                   productDto.quantity(),
                   productDto.category()
            );
        });

        mvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/products/1")))
                .andExpect(content().string(objectMapper.writeValueAsString(productDto)));
    }

    @Test
    public void testGetProduct() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(productDto);

        mvc.perform(get("/products/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(productDto)));
    }

    @Test
    public void testUpdateProduct() throws Exception {
        when(productService.getProductById(anyLong())).thenReturn(productDto);

        mvc.perform(put("/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProductDto)))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProduct() throws Exception {
        mvc.perform(delete("/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testSetImage() throws Exception {
        mvc.perform(put("/products/1/image")
                .contentType(MediaType.APPLICATION_JSON)
                .param("imageId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetImage() throws Exception {
        final ImageDto imageDto = new ImageDto(
                1L,
                "base64"
        );

        when(productService.getImage(anyLong())).thenReturn(Optional.of(imageDto));

        mvc.perform(get("/products/1/image"))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(imageDto)));

        when(productService.getImage(anyLong())).thenReturn(Optional.empty());

        mvc.perform(get("/products/1/image"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteImage() throws Exception {
        mvc.perform(delete("/products/1/image"))
                .andExpect(status().isOk());
    }
}
