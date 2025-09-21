package com.tarea.repositories;

import com.tarea.models.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;


public interface GuideRepository extends JpaRepository<Guide, Long> {
    List<Guide> findByCategory(String category);
    List<Guide> findByUser_Id(Long userId);
    List<Guide> findByCategoryAndUser_Id(String category, Long userId);
}
