package com.tarea.repositories;

import com.tarea.models.GuideHabit;
// Update the import path to the correct package where GuideHabitId is located
import com.tarea.models.GuideHabitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideHabitRepository extends JpaRepository<GuideHabit, GuideHabitId> {
}
