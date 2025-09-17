package com.tarea.dtos;

import lombok.Data;

@Data
public class HabitActivityListDTO {
    private Long id;
    private String name;
    private String category;
    private Long routineId;

}