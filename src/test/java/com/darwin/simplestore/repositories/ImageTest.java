// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.repositories;

import com.darwin.simplestore.TestcontainersConfiguration;
import com.darwin.simplestore.entities.Image;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@Import(TestcontainersConfiguration.class)
@DataJpaTest
@ActiveProfiles("dev")
public class ImageTest {
    @Autowired
    private ImageRepository imageRepository;

    @Test
    public void testAddImage() {
        Image image = new Image();
        image.setBase64Image("base64Image");

        assertDoesNotThrow(() -> imageRepository.save(image));
    }
}
