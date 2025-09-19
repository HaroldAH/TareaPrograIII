package com.tarea.dtos;

import java.util.List;

public class RoutinePageDTO {
    private List<RoutineDTO> content;
    private PageInfoDTO pageInfo;

    public RoutinePageDTO() {}
    public RoutinePageDTO(List<RoutineDTO> content, PageInfoDTO pageInfo) {
        this.content = content; this.pageInfo = pageInfo;
    }
    public List<RoutineDTO> getContent() { return content; }
    public void setContent(List<RoutineDTO> content) { this.content = content; }
    public PageInfoDTO getPageInfo() { return pageInfo; }
    public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
