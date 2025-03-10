// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record representing an image
 * @param id The id of the image
 * @param base64Content The base64 encoded image content
 */
@Schema(description = "An existing image")
public record ImageDto(
        @Schema(description = "The id of the image", example = "1")
        Long id,
        @Schema(description = "Bse64 encoded image content", example = "SGVsbG8gd29ybGQh")
        String base64Content) { }
