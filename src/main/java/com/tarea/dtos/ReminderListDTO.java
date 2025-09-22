package com.tarea.dtos;

import lombok.Data;

@Data
public class ReminderListDTO {
    private Long id;
    private String frequency;
    private HabitActivityListDTO habit;  
}