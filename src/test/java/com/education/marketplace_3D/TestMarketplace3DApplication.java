package com.education.marketplace_3D;

import org.springframework.boot.SpringApplication;

public class TestMarketplace3DApplication {

    public static void main(String[] args) {
        SpringApplication.from(Marketplace3DApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
