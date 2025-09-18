package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class RoutineDetailDTO {
    private Long id;
    private String title;
    private String daysOfWeek;
    private List<RoutineHabitDetailDTO> habits;
}
