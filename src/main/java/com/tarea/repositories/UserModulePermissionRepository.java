package com.tarea.repositories;

import com.tarea.models.Module;
import com.tarea.models.UserModulePermission;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserModulePermissionRepository extends JpaRepository<UserModulePermission, Long> {

     
    Optional<UserModulePermission> findByUserIdAndModule(Long userId, Module module);

    List<UserModulePermission> findAllByUserId(Long userId);

     
    List<UserModulePermission> findAllByUserIdIn(List<Long> userIds);

    @Modifying
    @Query("delete from UserModulePermission ump where ump.user.id = :userId")
    void deleteAllByUserId(@Param("userId") Long userId);
}
