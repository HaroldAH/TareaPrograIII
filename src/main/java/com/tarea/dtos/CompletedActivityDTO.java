package com.tarea.dtos;

import lombok.Data;

@Data
public class CompletedActivityDTO {
    private Long id;
    private Long progressLogId;
    private Long habitId;
    private String completedAt;
    private String notes;
}
