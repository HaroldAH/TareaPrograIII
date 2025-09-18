package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class GuideDetailDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
    private Long userId;
    private List<HabitActivityListDTO> recommendHabit;
}