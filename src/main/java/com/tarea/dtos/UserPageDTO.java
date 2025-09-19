package com.tarea.dtos;

import java.util.List;

public class UserPageDTO {
    private List<UserDTO> content;
    private PageInfoDTO pageInfo;
    public UserPageDTO() {}
    public UserPageDTO(List<UserDTO> content, PageInfoDTO pageInfo) {
        this.content = content; this.pageInfo = pageInfo;
    }
    public List<UserDTO> getContent() { return content; }
    public void setContent(List<UserDTO> content) { this.content = content; }
    public PageInfoDTO getPageInfo() { return pageInfo; }
    public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
