package com.tarea.dtos;

import lombok.Data;

@Data
public class CompletedActivityDTO {
    private Long id;
    private Long userId;
    private Long routineId;
    private Long habitId;
    private String date;           
    private String completedAt;   
    private String notes;
}
