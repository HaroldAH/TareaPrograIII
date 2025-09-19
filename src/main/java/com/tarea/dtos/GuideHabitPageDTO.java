package com.tarea.dtos;

import java.util.List;

public class GuideHabitPageDTO {
  private List<GuideHabitDTO> content;
  private PageInfoDTO pageInfo;
  public GuideHabitPageDTO() {}
  public GuideHabitPageDTO(List<GuideHabitDTO> content, PageInfoDTO pageInfo) {
    this.content = content; this.pageInfo = pageInfo;
  }
  public List<GuideHabitDTO> getContent(){ return content; }
  public void setContent(List<GuideHabitDTO> c){ this.content = c; }
  public PageInfoDTO getPageInfo(){ return pageInfo; }
  public void setPageInfo(PageInfoDTO p){ this.pageInfo = p; }
}
