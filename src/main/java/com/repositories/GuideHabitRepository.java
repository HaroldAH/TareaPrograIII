package com.repositories;

import com.tarea.models.GuideHabit;
import com.tarea.models.GuideHabitId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuideHabitRepository extends JpaRepository<GuideHabit, GuideHabitId> {
}
