package com.tarea.dtos;

import java.util.List;

public class RoutineHabitPageDTO {
  private List<RoutineHabitDTO> content;
  private PageInfoDTO pageInfo;
  public RoutineHabitPageDTO() {}
  public RoutineHabitPageDTO(List<RoutineHabitDTO> content, PageInfoDTO pageInfo) {
    this.content = content; this.pageInfo = pageInfo;
  }
  public List<RoutineHabitDTO> getContent(){ return content; }
  public void setContent(List<RoutineHabitDTO> c){ this.content = c; }
  public PageInfoDTO getPageInfo(){ return pageInfo; }
  public void setPageInfo(PageInfoDTO p){ this.pageInfo = p; }
}
