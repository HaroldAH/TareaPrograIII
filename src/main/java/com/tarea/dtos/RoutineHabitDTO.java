package com.tarea.dtos;

import lombok.Data;

@Data
public class RoutineHabitDTO {
    private Long routineId;
    private Long habitId;
    private Integer orderInRoutine;
    private String targetTimeInRoutine; // formato "HH:mm"
    private String notes;
}
