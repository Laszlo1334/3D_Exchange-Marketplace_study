package com.education.marketplace_3D;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "applicationAuditAware")
@EnableAsync
public class Marketplace3DApplication {

    public static void main(String[] args) {
        SpringApplication.run(Marketplace3DApplication.class, args);
    }

}
