package com.homeservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HomeServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomeServiceApplication.class, args);
        System.out.println("==============================================");
        System.out.println("  Home Service Backend Started!");
        System.out.println("  API Base URL : http://localhost:8080/api");
        System.out.println("  H2 Console   : http://localhost:8080/h2-console");
        System.out.println("==============================================");
    }
}
