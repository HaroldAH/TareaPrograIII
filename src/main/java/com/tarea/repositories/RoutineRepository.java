package com.tarea.repositories;

import com.tarea.models.Routine;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoutineRepository extends JpaRepository<Routine, Long> {
    List<Routine> findByUser_Id(Long userId);
}

