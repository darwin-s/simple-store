// Copyright (c) 2025 Dan Sirbu
// 
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Configuration class for spring data web
 */
@Configuration
@EnableSpringDataWebSupport(
        pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO
)
public class DataWebConfig {
}
