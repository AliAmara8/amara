package com.ali.amara;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.ali.amara")
public class AmaraApplication {

    public static void main(String[] args) {
        SpringApplication.run(AmaraApplication.class, args);
    }

}
