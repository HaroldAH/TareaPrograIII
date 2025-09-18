package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class CompletedRoutineInWeekDTO {
    private Long routineId;
    private String routineTitle;
    private List<CompletedActivityDTO> completedHabits;
}