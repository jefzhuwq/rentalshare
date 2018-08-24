package com.mediabox.rentalshare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class RentalshareApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentalshareApplication.class, args);
    }

}
