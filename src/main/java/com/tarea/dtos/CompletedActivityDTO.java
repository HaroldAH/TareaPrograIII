// com/tarea/dtos/CompletedActivityDTO.java
package com.tarea.dtos;

import lombok.Data;

@Data
public class CompletedActivityDTO {
    private Long id;
    private Long userId;
    private Long routineId;
    private Long habitId;
    private String date;        // "YYYY-MM-DD"
    private String completedAt; // "HH:mm"
    private Boolean isCompleted;
    private String notes;
}
