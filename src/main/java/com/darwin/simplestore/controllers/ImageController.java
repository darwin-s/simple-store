package com.darwin.simplestore.controllers;

import com.darwin.simplestore.dto.ImageDto;
import com.darwin.simplestore.dto.NewImageDto;
import com.darwin.simplestore.exceptions.ResourceNotFoundException;
import com.darwin.simplestore.services.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
@Tag(name = "Images", description = "Endpoints for managing images")
public class ImageController {
    private final ImageService imageService;

    /**
     * Add a new image
     * @param newImageDto The DTO representing the new image
     * @return The DTO of the new image
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Add new image", description = "Add a new image to the database, and return the created image")
    public ResponseEntity<ImageDto> addImage(
            @Parameter(description = "The contents of the new image")
            @RequestBody final NewImageDto newImageDto) {

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
    @GetMapping(value = "/{imageId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get image", description = "Get an existing image from the repository")
    public ResponseEntity<ImageDto> getImage(
            @Parameter(description = "The id of the image to be retrieved", example = "1")
            @PathVariable final Long imageId) throws ResourceNotFoundException {

        return ResponseEntity.ok(imageService.getImage(imageId));
    }

    /**
     * Update an image
     * @param imageId The id of the image
     * @param newImageDto The updated data of the image
     * @return Response object
     * @throws ResourceNotFoundException If the image could not be found
     */
    @PutMapping(value = "/{imageId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Update image", description = "Update the contents of an existing image")
    public ResponseEntity<Void> updateImage(
            @Parameter(description = "The id of the image to be updated", example = "1")
            @PathVariable final Long imageId,
            @Parameter(description = "The new contents to be put")
            @RequestBody final NewImageDto newImageDto) throws ResourceNotFoundException {

        final ImageDto imageDto = new ImageDto(
                imageId,
                newImageDto.base64Content()
        );

        imageService.updateImage(imageDto);
        return ResponseEntity.ok().build();
    }
}
