package com.tarea.repositories;

import com.tarea.models.Completedactivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CompletedActivityRepository extends JpaRepository<Completedactivity, Long> {

    List<Completedactivity> findByUser_Id(Long userId);
    List<Completedactivity> findByUser_IdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    // --- Agregaciones mensuales por categoría (AÑO COMPLETO) ---
    // Devuelve: [year, month, category, total_completions, unique_days, total_duration, weeks_active]
    @Query(value = """
        SELECT
           YEAR(ca.date)                        AS y,
           MONTH(ca.date)                       AS m,
           ha.category                          AS category,
           COUNT(*)                             AS total_completions,
           COUNT(DISTINCT ca.date)              AS unique_days,
           COALESCE(SUM(ha.duration),0)         AS total_duration,
           COUNT(DISTINCT YEARWEEK(ca.date, 3)) AS weeks_active
        FROM completedactivity ca
        JOIN habitactivity ha ON ha.id = ca.habit_id
        WHERE YEAR(ca.date) = :year
          AND (:userId IS NULL OR ca.user_id = :userId)
        GROUP BY YEAR(ca.date), MONTH(ca.date), ha.category
        ORDER BY m, category
    """, nativeQuery = true)
    List<Object[]> monthlyCategoryStatsYear(@Param("year") int year, @Param("userId") Long userId);

    @Query(value = """
        SELECT
           YEAR(ca.date)                        AS y,
           MONTH(ca.date)                       AS m,
           ha.category                          AS category,
           COUNT(*)                             AS total_completions,
           COUNT(DISTINCT ca.date)              AS unique_days,
           COALESCE(SUM(ha.duration),0)         AS total_duration,
           COUNT(DISTINCT YEARWEEK(ca.date, 3)) AS weeks_active
        FROM completedactivity ca
        JOIN habitactivity ha ON ha.id = ca.habit_id
        WHERE YEAR(ca.date) = :year
          AND MONTH(ca.date) = :month
          AND (:userId IS NULL OR ca.user_id = :userId)
        GROUP BY YEAR(ca.date), MONTH(ca.date), ha.category
        ORDER BY m, category
    """, nativeQuery = true)
    List<Object[]> monthlyCategoryStatsMonth(@Param("year") int year, @Param("month") int month, @Param("userId") Long userId);
}
