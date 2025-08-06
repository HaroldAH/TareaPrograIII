package com.tarea.dtos;

import lombok.Data;

@Data
public class GuideDTO {
    private Long id;
    private String title;
    private String content;
    private String category;
}