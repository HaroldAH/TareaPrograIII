package com.tarea.resolvers.inputs;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class PageRequestInput {
    private Integer page;   // 0-based
    private Integer size;   // tamaño de página, p.ej. 20
    private List<String> sort; // ["name,asc","id,desc"] o ["-name","+id"]

    public Integer getPage() { return page == null ? 0 : page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size == null ? 20 : size; }
    public void setSize(Integer size) { this.size = size; }

    public List<String> getSort() { return sort; }
    public void setSort(List<String> sort) { this.sort = sort; }

    public Pageable toPageable() {
        Sort s = Sort.unsorted();
        if (sort != null && !sort.isEmpty()) {
            Sort composed = Sort.unsorted();
            for (String token : sort) {
                if (token == null || token.isBlank()) continue;
                String t = token.trim();
                Sort next;
                if (t.startsWith("-")) {
                    next = Sort.by(t.substring(1)).descending();
                } else if (t.startsWith("+")) {
                    next = Sort.by(t.substring(1)).ascending();
                } else if (t.contains(",")) {
                    String[] parts = t.split(",", 2);
                    next = "desc".equalsIgnoreCase(parts[1].trim())
                        ? Sort.by(parts[0].trim()).descending()
                        : Sort.by(parts[0].trim()).ascending();
                } else {
                    next = Sort.by(t).ascending();
                }
                composed = composed == Sort.unsorted() ? next : composed.and(next);
            }
            s = composed;
        }
        return PageRequest.of(getPage(), getSize(), s);
    }
}
