package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewImageDto;
import com.darwin.simplestore.entities.Image;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.ImageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("dev")
public class ImageServiceTest {
    @Mock
    private ImageRepository imageRepository;

    @InjectMocks
    private ImageService imageService;

    private Image image;

    @BeforeEach
    public void setUp() {
        image = new Image(
                1L,
                "base64"
        );
    }

    @Test
    public void testAddImage() {
        when(imageRepository.save(any(Image.class))).thenReturn(image);

        final ImageDto imageDto = assertDoesNotThrow(() -> imageService.addImage(new NewImageDto("base64")));

        assertEquals(ImageService.toImageDto(image), imageDto);

        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    public void testGetImage() {
        when(imageRepository.findById(anyLong())).thenReturn(Optional.of(image));

        final ImageDto imageDto = assertDoesNotThrow(() -> imageService.getImage(image.getId()));
        assertEquals(ImageService.toImageDto(image), imageDto);

        verify(imageRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testGetImageException() {
        when(imageRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrowsExactly(ResourceNotFoundException.class, () -> imageService.getImage(image.getId()));

        verify(imageRepository, times(1)).findById(anyLong());
    }

    @Test
    public void testUpdateImage() {
        when(imageRepository.existsById(anyLong())).thenReturn(true);
        when(imageRepository.save(any(Image.class))).thenAnswer(i -> {
            final Image newImage = i.getArgument(0);
            image.setBase64Image(newImage.getBase64Image());

            return image;
        });

        assertDoesNotThrow(() -> imageService.updateImage(new ImageDto(1L, "test")));
        assertEquals("test", image.getBase64Image());

        verify(imageRepository, times(1)).existsById(anyLong());
        verify(imageRepository, times(1)).save(any(Image.class));
    }

    @Test
    public void testUpdateImageException() {
        when(imageRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> imageService.updateImage(new ImageDto(1L, "test")));

        verify(imageRepository, times(1)).existsById(anyLong());
    }

    @Test
    public void testDeleteImage() {
        when(imageRepository.existsById(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> imageService.deleteImage(image.getId()));

        verify(imageRepository, times(1)).existsById(anyLong());
        verify(imageRepository, times(1)).deleteById(anyLong());
    }

    @Test
    public void testDeleteImageException() {
        when(imageRepository.existsById(anyLong())).thenReturn(false);

        assertThrowsExactly(ResourceNotFoundException.class, () -> imageService.deleteImage(image.getId()));

        verify(imageRepository, times(1)).existsById(anyLong());
    }
}
