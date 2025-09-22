package com.tarea.dtos;

import lombok.Data;

@Data
public class HabitActivityDTO {
    private Long id;
    private String name;
    private String category;      
    private String description;
    private Integer duration;     
    private String targetTime;    
    private String notes;
}
