package com.darwin.simplestore.dto;

/**
 * Record representing an image
 * @param id The id of the image
 * @param base64Content The base64 encoded image content
 */
public record ImageDto(Long id, String base64Content) { }
