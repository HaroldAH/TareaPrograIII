package com.tarea.dtos;

import java.util.List;

public class RolePageDTO {
    private List<RoleDTO> content;
    private PageInfoDTO pageInfo;

    public RolePageDTO() {}
    public RolePageDTO(List<RoleDTO> content, PageInfoDTO pageInfo) {
        this.content = content; this.pageInfo = pageInfo;
    }
    public List<RoleDTO> getContent() { return content; }
    public void setContent(List<RoleDTO> content) { this.content = content; }
    public PageInfoDTO getPageInfo() { return pageInfo; }
    public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
