package com.darwin.simplestore.services;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewImageDto;
import com.darwin.simplestore.entities.Image;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.repositories.ImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class for managing images
 */
@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;

    /**
     * Add a new image to the database
     * @param newImageDto The DTO of the new image
     * @return DTO of the created image
     */
    public ImageDto addImage(final NewImageDto newImageDto) {
        return toImageDto(imageRepository.save(fromNewImageDto(newImageDto)));
    }

    /**
     * Get an image
     * @param id The id of the image
     * @return DTO of the requested image
     * @throws ResourceNotFoundException If the image could not be found
     */
    public ImageDto getImage(final Long id) throws ResourceNotFoundException {
        return toImageDto(imageRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No image found with id: " + id)));
    }

    /**
     * Update an image
     * @param imageDto The data of the updated image
     * @throws ResourceNotFoundException If the requested image does not exist
     */
    public void updateImage(final ImageDto imageDto) throws ResourceNotFoundException {
        if (!imageRepository.existsById(imageDto.id())) {
            throw new ResourceNotFoundException("No image found with id: " + imageDto.id());
        }

        final Image image = new Image(
                imageDto.id(),
                imageDto.base64Content()
        );

        imageRepository.save(image);
    }

    /**
     * Delete an image
     * @param id The id of the image
     * @throws ResourceNotFoundException If the requested image does not exist
     */
    public void deleteImage(final Long id) throws ResourceNotFoundException {
        if (!imageRepository.existsById(id)) {
            throw new ResourceNotFoundException("No image found with id: " + id);
        }

        imageRepository.deleteById(id);
    }

    /**
     * Convert a new image DTO to n entity
     * @param newImageDto The new image DTO
     * @return Entity for that DTO
     */
    public static Image fromNewImageDto(final NewImageDto newImageDto) {
        Image image = new Image();
        image.setBase64Image(newImageDto.base64Content());

        return image;
    }

    /**
     * Convert an image entity to a DTO
     * @param image The image entity
     * @return An image DTO
     */
    public static ImageDto toImageDto(final Image image) {
        return new ImageDto(
                image.getId(),
                image.getBase64Image()
        );
    }
}
