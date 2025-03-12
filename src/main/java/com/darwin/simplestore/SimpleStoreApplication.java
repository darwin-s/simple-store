// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the application
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "Simple Store",
                version = "1.1.0",
                description = "A simple store API",
                license = @License(
                        name = "MIT License",
                        url = "https://opensource.org/license/MIT"
                )
        )
)
public class SimpleStoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(SimpleStoreApplication.class, args);
    }

}
