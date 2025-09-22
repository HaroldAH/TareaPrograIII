package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class CompletedWeekDTO {
    private String weekLabel;  
    private List<CompletedRoutineInWeekDTO> routines;
    private int totalCompleted;
}