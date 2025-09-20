package com.tarea.services;

import com.tarea.dtos.UserDTO;
import com.tarea.dtos.UserPermissionDTO;
import com.tarea.models.Module;
import com.tarea.models.ModulePermission;
import com.tarea.models.User;
import com.tarea.models.UserModulePermission;
import com.tarea.repositories.UserModulePermissionRepository;
import com.tarea.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.tarea.security.SecurityUtils.requireMutate;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserModulePermissionRepository umpRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       UserModulePermissionRepository umpRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.umpRepository = umpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /* ========= bootstrap & CRUD básico ========= */

    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    @Transactional
    public UserDTO createUser(String name, String email, String rawPassword, Boolean isAuditor, List<UserPermissionDTO> permissions) {
        boolean bootstrap = (userRepository.count() == 0);

        if (!bootstrap) {
            requireMutate(Module.USERS);
        }

        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);

        User u = new User();
        u.setName(name);
        u.setEmail(normalizedEmail);
        u.setPassword(rawPassword != null ? passwordEncoder.encode(rawPassword) : null);
        u.setIsAuditor(Boolean.TRUE.equals(isAuditor));

        if (bootstrap && (permissions == null || permissions.isEmpty())) {
            u.setIsAuditor(true);
        }

        u = userRepository.save(u);

        // Permisos
        if (permissions != null && !permissions.isEmpty()) {
            for (UserPermissionDTO p : permissions) {
                // DTO de clase → getters
                upsertPermissionInternal(u, p.getModule(), p.getPermission());
            }
        } else if (bootstrap) {
            upsertPermissionInternal(u, Module.USERS, ModulePermission.MUTATE);
        }

        return toDTO(u, loadPerms(u.getId()));
    }

    @Transactional
    public UserDTO upsertPermission(Long userId, Module module, ModulePermission permission) {
        requireMutate(Module.USERS);

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no existe: " + userId));

        upsertPermissionInternal(u, module, permission);
        return toDTO(u, loadPerms(userId));
    }

    @Transactional
    public UserDTO revokePermission(Long userId, Module module) {
        requireMutate(Module.USERS);

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no existe: " + userId));

        umpRepository.findByUserIdAndModule(userId, module)
                .ifPresent(umpRepository::delete);

        return toDTO(u, loadPerms(userId));
    }

    @Transactional
    public UserDTO setAuditor(Long userId, boolean auditor) {
        requireMutate(Module.USERS);

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no existe: " + userId));

        u.setIsAuditor(auditor);
        userRepository.save(u);

        return toDTO(u, loadPerms(userId));
    }

    @Transactional
    public void delete(Long id) {
        requireMutate(Module.USERS);
        umpRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    /* ========= helpers ========= */

    @Transactional(readOnly = true)
    private List<UserPermissionDTO> loadPerms(Long userId) {
        return umpRepository.findAllByUserId(userId).stream()
                .map(p -> {
                    UserPermissionDTO dto = new UserPermissionDTO();
                    dto.setModule(p.getModule());
                    dto.setPermission(p.getPermission());
                    return dto;
                })
                .toList();
    }

    // upsert interno (sin requireMutate) para bootstrap y usos internos seguros
    @Transactional
    private void upsertPermissionInternal(User user, Module module, ModulePermission permission) {
        var existing = umpRepository.findByUserIdAndModule(user.getId(), module);
        if (existing.isPresent()) {
            var e = existing.get();
            e.setPermission(permission);
            umpRepository.save(e);
        } else {
            umpRepository.save(new UserModulePermission(user, module, permission));
        }
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        List<User> users = userRepository.findAll();
        if (users.isEmpty()) return List.of();

        List<Long> ids = users.stream().map(User::getId).toList();
        List<UserModulePermission> perms = umpRepository.findAllByUserIdIn(ids);

        Map<Long, List<UserPermissionDTO>> permsByUser = perms.stream()
                .collect(Collectors.groupingBy(
                        p -> p.getUser().getId(),
                        Collectors.mapping(p -> {
                                    UserPermissionDTO dto = new UserPermissionDTO();
                                    dto.setModule(p.getModule());
                                    dto.setPermission(p.getPermission());
                                    return dto;
                                },
                                Collectors.toList()
                        )
                ));

        return users.stream()
                .map(u -> toDTO(u, permsByUser.getOrDefault(u.getId(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        User u = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no existe: " + id));
        return toDTO(u, loadPerms(id));
    }

    // ⬇⬇⬇ AQUÍ EL CAMBIO CLAVE: construir DTO con setters
    private UserDTO toDTO(User u, List<UserPermissionDTO> perms) {
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        // Si tu User tiene getIsAuditor() (Boolean) → usa esa:
        dto.setIsAuditor(Boolean.TRUE.equals(u.getIsAuditor()));
        // Si tu User es boolean isAuditor y el getter es isAuditor(), usa:
        // dto.setIsAuditor(u.isAuditor());

        dto.setPermissions(perms);
        return dto;
    }
}
