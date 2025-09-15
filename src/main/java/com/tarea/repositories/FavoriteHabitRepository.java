package com.tarea.repositories;

import com.tarea.models.FavoriteHabit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteHabitRepository extends JpaRepository<FavoriteHabit, Long> {
    // Puedes agregar consultas personalizadas después si lo necesitas
}
