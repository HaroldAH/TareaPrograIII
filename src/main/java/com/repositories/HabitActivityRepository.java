package com.repositories;

import com.tarea.models.Habitactivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitActivityRepository extends JpaRepository<Habitactivity, Long> {
}
