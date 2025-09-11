package com.tarea.repositories;

import com.tarea.models.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByUser_Id(Long userId);
    Optional<Reminder> findByIdAndUser_Id(Long id, Long userId);
}
