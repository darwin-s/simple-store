package com.darwin.simplestore.dto;

/**
 * Record representing an image
 * @param base64Content Base64 encoded content of the image
 */
public record NewImageDto(String base64Content) { }
