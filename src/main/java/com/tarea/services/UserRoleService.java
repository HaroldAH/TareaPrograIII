package com.tarea.services;

import com.tarea.dtos.UserRoleDTO;
import com.tarea.models.Role;
import com.tarea.models.User;
import com.tarea.models.UserRole;
import com.tarea.models.UserRoleId;
import com.tarea.repositories.RoleRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.repositories.UserRoleRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserRoleService(UserRoleRepository userRoleRepository,
                           UserRepository userRepository,
                           RoleRepository roleRepository) {
        this.userRoleRepository = userRoleRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    public List<UserRoleDTO> getAll() {
        return userRoleRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserRoleDTO getById(Long userId, Long roleId) {
        UserRoleId id = new UserRoleId();
        id.setUserId(userId);
        id.setRoleId(roleId);
        return userRoleRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public UserRoleDTO save(UserRoleDTO dto) {
        UserRoleId id = new UserRoleId();
        id.setUserId(dto.getUserId());
        id.setRoleId(dto.getRoleId());
        UserRole entity = new UserRole();
        entity.setId(id);

        Optional<User> user = userRepository.findById(dto.getUserId());
        Optional<Role> role = roleRepository.findById(dto.getRoleId());

        entity.setUser(user.orElse(null));
        entity.setRole(role.orElse(null));

        UserRole saved = userRoleRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long userId, Long roleId) {
        UserRoleId id = new UserRoleId();
        id.setUserId(userId);
        id.setRoleId(roleId);
        userRoleRepository.deleteById(id);
    }

    private UserRoleDTO toDTO(UserRole entity) {
        UserRoleDTO dto = new UserRoleDTO();
        dto.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        dto.setRoleId(entity.getRole() != null ? entity.getRole().getId() : null);
        return dto;
    }
}
