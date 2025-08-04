package com.repositories;

import com.tarea.models.Authtoken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<Authtoken, String> {
}
