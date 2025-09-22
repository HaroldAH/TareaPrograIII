package com.tarea.dtos;

import lombok.Data;
import java.util.List;

@Data
public class CompletedDayDTO {
    private String date;  
    private List<CompletedActivityDTO> activities;
    private int totalCompleted;
}