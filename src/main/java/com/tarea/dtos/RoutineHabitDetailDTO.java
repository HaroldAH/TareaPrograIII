package com.tarea.dtos;

import lombok.Data;

@Data
public class RoutineHabitDetailDTO {
    private Long habitId;
    private String name;
    private String category;
    private String description;
    private Integer orderInRoutine;
    private String targetTimeInRoutine;
    private String notes;
}
