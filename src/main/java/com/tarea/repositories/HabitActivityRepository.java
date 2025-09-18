package com.tarea.repositories;

import com.tarea.models.Habitactivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HabitActivityRepository extends JpaRepository<Habitactivity, Long> {
    List<Habitactivity> findByCategory(String category);
    Optional<Habitactivity> findByName(String name);
}
