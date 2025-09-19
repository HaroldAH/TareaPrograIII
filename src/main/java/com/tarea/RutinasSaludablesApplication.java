package com.tarea;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.tarea") // Esto asegura que se escanee todo
public class RutinasSaludablesApplication {

    public static void main(String[] args) {
        SpringApplication.run(RutinasSaludablesApplication.class, args);
    }
}
