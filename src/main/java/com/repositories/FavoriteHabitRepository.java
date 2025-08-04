package com.repositories;

import com.tarea.models.Favoritehabit;
import com.tarea.models.FavoritehabitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteHabitRepository extends JpaRepository<Favoritehabit, FavoritehabitId> {
}
