package com.tarea.dtos;

import java.util.List;

public class GuidePageDTO {
    private List<GuideDTO> content;
    private PageInfoDTO pageInfo;

    public GuidePageDTO() {}
    public GuidePageDTO(List<GuideDTO> content, PageInfoDTO pageInfo) {
        this.content = content;
        this.pageInfo = pageInfo;
    }

    public List<GuideDTO> getContent() { return content; }
    public void setContent(List<GuideDTO> content) { this.content = content; }
    public PageInfoDTO getPageInfo() { return pageInfo; }
    public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
