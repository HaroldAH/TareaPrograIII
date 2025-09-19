package com.tarea.dtos;

import java.util.List;

public class FavoriteHabitPageDTO {
  private List<FavoriteHabitDTO> content;
  private PageInfoDTO pageInfo;
  public FavoriteHabitPageDTO() {}
  public FavoriteHabitPageDTO(List<FavoriteHabitDTO> content, PageInfoDTO pageInfo) {
    this.content = content; this.pageInfo = pageInfo;
  }
  public List<FavoriteHabitDTO> getContent() { return content; }
  public void setContent(List<FavoriteHabitDTO> content) { this.content = content; }
  public PageInfoDTO getPageInfo() { return pageInfo; }
  public void setPageInfo(PageInfoDTO pageInfo) { this.pageInfo = pageInfo; }
}
