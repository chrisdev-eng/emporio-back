package com.chrisdev.eng.pdvediel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PdvEdielApplication {

    public static void main(String[] args) {
        SpringApplication.run(PdvEdielApplication.class, args);
    }

}