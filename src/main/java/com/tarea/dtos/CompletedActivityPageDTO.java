package com.tarea.dtos;

import java.util.List;

public class CompletedActivityPageDTO {
    private List<CompletedActivityDTO> content;
    private PageInfoDTO pageInfo;

    public CompletedActivityPageDTO() {}
    public CompletedActivityPageDTO(List<CompletedActivityDTO> content, PageInfoDTO pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    public List<CompletedActivityDTO> getContent() { return content; }
    public void setContent(List<CompletedActivityDTO> content) { this.content = content; }
    public PageInfoDTO getPageInfo() { return pageInfo; }
    public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
