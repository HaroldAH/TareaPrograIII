// src/main/java/com/tarea/repositories/PasswordResetTokenRepository.java
package com.tarea.repositories;

import com.tarea.models.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    long deleteByUser_Id(Long userId);
    long deleteByExpiresAtBefore(Instant now);
}
