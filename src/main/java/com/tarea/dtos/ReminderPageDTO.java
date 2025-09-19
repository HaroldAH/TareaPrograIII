package com.tarea.dtos;

import java.util.List;

public class ReminderPageDTO {
  private List<ReminderDTO> content;
  private PageInfoDTO pageInfo;
  public ReminderPageDTO() {}
  public ReminderPageDTO(List<ReminderDTO> content, PageInfoDTO pageInfo) {
    this.content = content; this.pageInfo = pageInfo;
  }
  public List<ReminderDTO> getContent() { return content; }
  public void setContent(List<ReminderDTO> content) { this.content = content; }
  public PageInfoDTO getPageInfo() { return pageInfo; }
  public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
