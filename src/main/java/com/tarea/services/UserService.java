package com.tarea.services;

import com.tarea.dtos.PageInfoDTO;
import com.tarea.dtos.UserDTO;
import com.tarea.dtos.UserPageDTO;
import com.tarea.dtos.UserPermissionDTO;
import com.tarea.models.Module;
import com.tarea.models.ModulePermission;
import com.tarea.models.User;
import com.tarea.models.UserModulePermission;
import com.tarea.repositories.UserModulePermissionRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.InputSanitizationUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    /** Firma LEGADA para compatibilidad con llamadas existentes. */
    @Transactional
    public UserDTO createUser(String name, String email, String rawPassword,
                              Boolean isAuditor, List<UserPermissionDTO> permissions) {
        return createUser(name, email, rawPassword, isAuditor, permissions, null, null);
    }

    /** Nuevo create con soporte Coach + asignación. Aplica XOR Auditor/Coach. */
    @Transactional
    public UserDTO createUser(String name, String email, String rawPassword,
                              Boolean isAuditor, List<UserPermissionDTO> permissions,
                              Boolean isCoach, Long assignedCoachId) {
        boolean bootstrap = (userRepository.count() == 0);
        if (!bootstrap) {
            requireMutate(Module.USERS);
        }

        String normalizedEmail = (email == null) ? null : email.trim().toLowerCase(Locale.ROOT);

        boolean auditor = Boolean.TRUE.equals(isAuditor);
        boolean coach   = Boolean.TRUE.equals(isCoach);
        if (auditor && coach) {
            throw new IllegalStateException("No puede ser Auditor y Coach al mismo tiempo.");
        }

        User u = new User();
        u.setName(name);
        u.setEmail(normalizedEmail);
        u.setPassword(rawPassword != null ? passwordEncoder.encode(rawPassword) : null);
        u.setIsAuditor(auditor);
        u.setIsCoach(coach);

        // Bootstrap por defecto: si no hay permisos y no es coach, deja auditor=true
        if (bootstrap && (permissions == null || permissions.isEmpty()) && !Boolean.TRUE.equals(u.getIsCoach())) {
            u.setIsAuditor(true);
        }

        // Asignación opcional de coach
        if (assignedCoachId != null) {
            User c = userRepository.findById(assignedCoachId)
                    .orElseThrow(() -> new NoSuchElementException("Coach no existe: " + assignedCoachId));
            if (!Boolean.TRUE.equals(c.getIsCoach())) {
                throw new IllegalStateException("El usuario destino no tiene flag Coach.");
            }
            u.setAssignedCoach(c);
        }

        u = userRepository.save(u);

        // Permisos por módulo
        if (permissions != null && !permissions.isEmpty()) {
            for (UserPermissionDTO p : permissions) {
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

        // XOR con Coach
        if (auditor && Boolean.TRUE.equals(u.getIsCoach())) {
            throw new IllegalStateException("No puede ser Auditor y Coach a la vez.");
        }

        u.setIsAuditor(auditor);
        userRepository.save(u);

        return toDTO(u, loadPerms(userId));
    }

    /** NUEVO: toggle Coach con XOR */
    @Transactional
    public UserDTO setCoach(Long userId, boolean coach) {
        requireMutate(Module.USERS);

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no existe: " + userId));

        if (coach && Boolean.TRUE.equals(u.getIsAuditor())) {
            throw new IllegalStateException("No puede ser Auditor y Coach a la vez.");
        }

        u.setIsCoach(coach);
        userRepository.save(u);

        return toDTO(u, loadPerms(userId));
    }

    /** NUEVO: asignar un Coach a un usuario (1→N) */
    @Transactional
    public UserDTO setUserCoach(Long userId, Long coachId) {
        requireMutate(Module.USERS);

        User u = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("Usuario no existe: " + userId));
        User c = userRepository.findById(coachId)
                .orElseThrow(() -> new NoSuchElementException("Coach no existe: " + coachId));

        if (!Boolean.TRUE.equals(c.getIsCoach())) {
            throw new IllegalStateException("El destino no es Coach.");
        }
        if (u.getId().equals(c.getId())) {
            throw new IllegalStateException("Un usuario no puede ser su propio coach.");
        }

        u.setAssignedCoach(c);
        userRepository.save(u);

        return toDTO(u, loadPerms(userId));
    }

    @Transactional
    public void delete(Long id) {
        requireMutate(Module.USERS);
        umpRepository.deleteAllByUserId(id);
        userRepository.deleteById(id);
    }

    /* ========= Paginación ========= */

    @Transactional(readOnly = true)
    public UserPageDTO pageUsers(Pageable pageable) {
        Page<User> p = userRepository.findAll(pageable);

        // Página vacía
        if (p.isEmpty()) {
            return new UserPageDTO(
                    List.of(),
                    new PageInfoDTO(
                            0, // totalElements
                            0, // totalPages
                            p.getNumber(),
                            p.getSize(),
                            0, // numberOfElements
                            false, // hasNext
                            false  // hasPrevious
                    )
            );
        }

        // Carga permisos en bloque
        List<Long> ids = p.getContent().stream().map(User::getId).toList();
        List<UserModulePermission> perms = umpRepository.findAllByUserIdIn(ids);

        Map<Long, List<UserPermissionDTO>> permsByUser = perms.stream()
                .collect(Collectors.groupingBy(
                        up -> up.getUser().getId(),
                        Collectors.mapping(up -> {
                                    UserPermissionDTO dto = new UserPermissionDTO();
                                    dto.setModule(up.getModule());
                                    dto.setPermission(up.getPermission());
                                    return dto;
                                },
                                Collectors.toList()
                        )
                ));

        List<UserDTO> content = p.getContent().stream()
                .map(u -> toDTO(u, permsByUser.getOrDefault(u.getId(), List.of())))
                .toList();

        PageInfoDTO info = new PageInfoDTO(
                (int) p.getTotalElements(),
                p.getTotalPages(),
                p.getNumber(),
                p.getSize(),
                p.getNumberOfElements(),
                p.hasNext(),
                p.hasPrevious()
        );

        return new UserPageDTO(content, info);
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

    /** NUEVO: lista de usuarios asignados a un coach (no paginado) */
    @Transactional(readOnly = true)
    public List<UserDTO> getCoachees(Long coachId) {
        return userRepository.findByAssignedCoach_Id(coachId).stream()
                .map(u -> toDTO(u, loadPerms(u.getId())))
                .toList();
    }

    // Construcción del DTO con flags de Coach y asignación
    private UserDTO toDTO(User user, List<UserPermissionDTO> permissions) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAssignedCoachId(user.getAssignedCoach() != null ? user.getAssignedCoach().getId() : null);
        dto.setPermissions(permissions);

        // Asegura que nunca sean null
        dto.setIsAuditor(user.getIsAuditor() != null ? user.getIsAuditor() : false);
        dto.setIsCoach(user.getIsCoach() != null ? user.getIsCoach() : false);

        return dto;
    }





    @Transactional(readOnly = true)
public UserPageDTO pageCoachees(Long coachId, Pageable pageable) {
    Page<User> p = userRepository.findByAssignedCoach_Id(coachId, pageable);

    if (p.isEmpty()) {
        return new UserPageDTO(
            List.of(),
            new PageInfoDTO(0, 0, p.getNumber(), p.getSize(), 0, false, false)
        );
    }

    // Carga permisos en bloque
    List<Long> ids = p.getContent().stream().map(User::getId).toList();
    List<UserModulePermission> perms = umpRepository.findAllByUserIdIn(ids);

    Map<Long, List<UserPermissionDTO>> permsByUser = perms.stream()
        .collect(Collectors.groupingBy(
            up -> up.getUser().getId(),
            Collectors.mapping(up -> {
                UserPermissionDTO dto = new UserPermissionDTO();
                dto.setModule(up.getModule());
                dto.setPermission(up.getPermission());
                return dto;
            }, Collectors.toList())
        ));

    List<UserDTO> content = p.getContent().stream()
        .map(u -> toDTO(u, permsByUser.getOrDefault(u.getId(), List.of())))
        .toList();

    PageInfoDTO info = new PageInfoDTO(
        (int) p.getTotalElements(),
        p.getTotalPages(),
        p.getNumber(),
        p.getSize(),
        p.getNumberOfElements(),
        p.hasNext(),
        p.hasPrevious()
    );

    return new UserPageDTO(content, info);
}

/** Registro público: crea usuario con permisos mínimos (solo para sí mismo) */
@Transactional
public UserDTO register(String name, String email, String rawPassword) {
    // Validación manual de campos vulnerables
    if (InputSanitizationUtils.containsMaliciousPattern(name)) {
        throw new IllegalArgumentException("Malicious input detected in name");
    }
    if (InputSanitizationUtils.containsMaliciousPattern(email)) {
        throw new IllegalArgumentException("Malicious input detected in email");
    }
    if (InputSanitizationUtils.containsMaliciousPattern(rawPassword)) {
        throw new IllegalArgumentException("Malicious input detected in password");
    }

    String normalizedEmail = (email == null) ? null : email.trim().toLowerCase(Locale.ROOT);

    User u = new User();
    u.setName(name);
    u.setEmail(normalizedEmail);
    u.setPassword(rawPassword != null ? passwordEncoder.encode(rawPassword) : null);
    u.setIsAuditor(false);
    u.setIsCoach(false);

    u = userRepository.save(u);

    List<UserPermissionDTO> basicPermissions = List.of(
        createPerm(Module.HABITS, ModulePermission.CONSULT),
        createPerm(Module.HABITS, ModulePermission.MUTATE),
        createPerm(Module.ROUTINES, ModulePermission.CONSULT),
        createPerm(Module.ROUTINES, ModulePermission.MUTATE),
        createPerm(Module.REMINDERS, ModulePermission.CONSULT),
        createPerm(Module.REMINDERS, ModulePermission.MUTATE)
    );

    for (UserPermissionDTO perm : basicPermissions) {
        upsertPermissionInternal(u, perm.getModule(), perm.getPermission());
    }

    return toDTO(u, loadPerms(u.getId()));
}

// Helper para crear UserPermissionDTO fácilmente
private UserPermissionDTO createPerm(Module module, ModulePermission permission) {
    UserPermissionDTO dto = new UserPermissionDTO();
    dto.setModule(module);
    dto.setPermission(permission);
    return dto;
}
}
