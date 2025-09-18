package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class CompletedWeekDTO {
    private String weekLabel; // Ej: "2025-W38"
    private List<CompletedRoutineInWeekDTO> routines;
    private int totalCompleted;
}