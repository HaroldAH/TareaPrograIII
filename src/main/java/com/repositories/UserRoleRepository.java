package com.repositories;

import com.tarea.models.UserRole;
// Update the import path to the correct location of UserRoleId
import com.tarea.models.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
