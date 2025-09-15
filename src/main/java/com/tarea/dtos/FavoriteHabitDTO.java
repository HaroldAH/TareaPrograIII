package com.tarea.dtos;

import lombok.Data;

@Data
public class FavoriteHabitDTO {
    private Long id;
    private Long userId;
    private Long habitId;
}
