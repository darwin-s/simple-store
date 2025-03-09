package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewImageDto;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Controller for images
 */
@RestController
@RequestMapping("/images")
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    /**
     * Add a new image
     * @param newImageDto The DTO representing the new image
     * @return The DTO of the new image
     */
    @PostMapping
    public ResponseEntity<ImageDto> addImage(@RequestBody final NewImageDto newImageDto) {
        final ImageDto imageDto = imageService.addImage(newImageDto);
        final URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{imageId}")
                .buildAndExpand(imageDto.id())
                .toUri();

        return ResponseEntity.created(location).body(imageDto);
    }

    /**
     * Get an image
     * @param imageId The id of the image
     * @return The image DTO
     * @throws ResourceNotFoundException If the image could not be found
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<ImageDto> getImage(@PathVariable final Long imageId) throws ResourceNotFoundException {
        return ResponseEntity.ok(imageService.getImage(imageId));
    }

    /**
     * Update an image
     * @param imageId The id of the image
     * @param newImageDto The updated data of the image
     * @return Response object
     * @throws ResourceNotFoundException If the image could not be found
     */
    @PutMapping("/{imageId}")
    public ResponseEntity<Void> updateImage(@PathVariable final Long imageId, @RequestBody final NewImageDto newImageDto) throws ResourceNotFoundException {
        final ImageDto imageDto = new ImageDto(
                imageId,
                newImageDto.base64Content()
        );

        imageService.updateImage(imageDto);
        return ResponseEntity.ok().build();
    }
}
