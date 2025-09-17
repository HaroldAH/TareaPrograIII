package com.tarea.repositories;

import com.tarea.models.Completedactivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CompletedActivityRepository extends JpaRepository<Completedactivity, Long> {
    List<Completedactivity> findByUser_Id(Long userId);
    List<Completedactivity> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}
