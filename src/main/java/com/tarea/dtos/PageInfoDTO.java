package com.tarea.dtos;

public class PageInfoDTO {
    private int totalElements;
    private int totalPages;
    private int page;
    private int size;
    private int numberOfElements;
    private boolean hasNext;
    private boolean hasPrevious;

    public PageInfoDTO() {}

    public PageInfoDTO(int totalElements, int totalPages, int page, int size,
                       int numberOfElements, boolean hasNext, boolean hasPrevious) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
        this.numberOfElements = numberOfElements;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public int getTotalElements() { return totalElements; }
    public void setTotalElements(int totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public int getNumberOfElements() { return numberOfElements; }
    public void setNumberOfElements(int numberOfElements) { this.numberOfElements = numberOfElements; }
    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}
