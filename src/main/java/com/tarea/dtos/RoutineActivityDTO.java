package com.tarea.dtos;

import lombok.Data;

@Data
public class RoutineActivityDTO {
    private Long id;
    private Long routineId;
    private Long habitId;
    private Integer duration;
    private String targetTime;
    private String notes;
}
