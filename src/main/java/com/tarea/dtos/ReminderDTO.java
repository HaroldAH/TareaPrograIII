package com.tarea.dtos;

import lombok.Data;

@Data
public class ReminderDTO {
    private Long id;
    private Long userId;
    private Long habitId;
    private String time;       
    private String frequency;   
}
