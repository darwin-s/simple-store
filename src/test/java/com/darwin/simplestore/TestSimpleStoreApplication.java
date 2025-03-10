// Copyright (c) 2025 Dan Sirbu
//
// This software is released under the MIT License.
// https://opensource.org/licenses/MIT

package com.darwin.simplestore;

import org.springframework.boot.SpringApplication;

public class TestSimpleStoreApplication {

    public static void main(String[] args) {
        SpringApplication.from(SimpleStoreApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
