package com.banknova;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BanknovaApplication {

    public static void main(String[] args) {
        SpringApplication.run(BanknovaApplication.class, args);
    }

}
