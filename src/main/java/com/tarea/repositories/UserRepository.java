package com.tarea.repositories;

import com.tarea.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

// ⬇️ imports para paginación
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByName(String name);

    // Lista NO paginada de usuarios asignados a un coach
    List<User> findByAssignedCoach_Id(Long coachId);

    // ⬅️ Necesario para myCoacheesPage / coacheesPage
    Page<User> findByAssignedCoach_Id(Long coachId, Pageable pageable);
}
