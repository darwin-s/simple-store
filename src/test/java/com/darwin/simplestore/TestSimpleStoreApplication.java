package com.darwin.simplestore;

import org.springframework.boot.SpringApplication;

public class TestSimpleStoreApplication {

    public static void main(String[] args) {
        SpringApplication.from(SimpleStoreApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
