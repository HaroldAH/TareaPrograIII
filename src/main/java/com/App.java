package com;

import com.TestEntity;
import com.TestEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public CommandLineRunner testDatabase(TestEntityRepository repo) {
        return args -> {
            TestEntity entidad = new TestEntity();
            entidad.setNombre("Conexión OK");
            repo.save(entidad);
            System.out.println("✅ Registro guardado correctamente en la base de datos.");
        };
    }

    mmmm
}