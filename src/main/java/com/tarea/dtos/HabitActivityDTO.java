package com.tarea.dtos;

import lombok.Data;

@Data
public class HabitActivityDTO {
    private Long id;
    private String name;
    private String category;     // PHYSICAL, MENTAL, SLEEP, DIET
    private String description;
    private Integer duration;    // En minutos
    private String targetTime;   // Formato: "HH:mm" como string para GraphQL
    private String notes;
    private Boolean isFavorite;
}
