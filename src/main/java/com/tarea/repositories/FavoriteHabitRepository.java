package com.tarea.repositories;

import com.tarea.models.FavoriteHabit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteHabitRepository extends JpaRepository<FavoriteHabit, Long> {
    List<FavoriteHabit> findByUser_Id(Long userId);
    List<FavoriteHabit> findByUser_IdAndHabit_Category(Long userId, String category);
    FavoriteHabit findByUser_IdAndHabit_Name(Long userId, String name);
}
