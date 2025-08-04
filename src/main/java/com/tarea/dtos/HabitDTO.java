package com.tarea.dtos;

import lombok.Data;

@Data
public class HabitDTO {
    private Long id;
    private String name;
    private String category;
    private String description;
}

