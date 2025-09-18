package com.tarea.repositories;

import com.tarea.models.RoutineHabit;
import com.tarea.models.RoutineHabitId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutineHabitRepository extends JpaRepository<RoutineHabit, RoutineHabitId> {
    List<RoutineHabit> findByRoutine_Id(Long routineId);

    List<RoutineHabit> findByHabit_Id(Long habitId);

    List<RoutineHabit> findByRoutine_IdOrderByOrderInRoutine(Long routineId);
}
