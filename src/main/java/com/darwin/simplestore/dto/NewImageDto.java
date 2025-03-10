// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Record representing an image
 * @param base64Content Base64 encoded content of the image
 */
@Schema(description = "A new image to be create")
public record NewImageDto(
        @Schema(description = "The base64 encoded content of the image", example = "SGVsbG8gd29ybGQh")
        String base64Content) { }
