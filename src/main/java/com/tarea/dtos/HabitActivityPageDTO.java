package com.tarea.dtos;

import java.util.List;

public class HabitActivityPageDTO {
    private List<HabitActivityDTO> content;
    private PageInfoDTO pageInfo;

    public HabitActivityPageDTO() {}
    public HabitActivityPageDTO(List<HabitActivityDTO> content, PageInfoDTO pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    public List<HabitActivityDTO> getContent() { return content; }
    public void setContent(List<HabitActivityDTO> content) { this.content = content; }
    public PageInfoDTO getPageInfo() { return pageInfo; }
    public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
