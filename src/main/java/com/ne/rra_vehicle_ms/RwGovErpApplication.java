package com.ne.rra_vehicle_ms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class RwGovErpApplication {

    public static void main(String[] args) {
        SpringApplication.run(RwGovErpApplication.class, args);
    }

}
