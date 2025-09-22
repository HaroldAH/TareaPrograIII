package com.tarea.services;

import com.tarea.dtos.UserDTO;
import com.tarea.dtos.UserPermissionDTO;
import com.tarea.models.Module;
import com.tarea.models.ModulePermission;
import com.tarea.models.User;
import com.tarea.models.UserModulePermission;
import com.tarea.repositories.UserModulePermissionRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.SecurityUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private UserModulePermissionRepository umpRepository;
    private PasswordEncoder passwordEncoder;
    private UserService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        umpRepository = mock(UserModulePermissionRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new UserService(userRepository, umpRepository, passwordEncoder);
    }

    @Test
void createUser_bootstrap_setsAuditorAndUsersMutatePerm() {
    // bootstrap: no hay usuarios aún
    when(userRepository.count()).thenReturn(0L);
    when(passwordEncoder.encode("123")).thenReturn("enc-123");
    when(userRepository.save(any(User.class))).thenAnswer(inv -> {
        User u = inv.getArgument(0);
        u.setId(1L);
        return u;
    });
    when(umpRepository.findByUserIdAndModule(eq(1L), eq(Module.USERS))).thenReturn(Optional.empty());
    when(umpRepository.save(any(UserModulePermission.class))).thenAnswer(inv -> inv.getArgument(0));

    // permissions = null (o List.of())
    UserDTO dto = service.createUser("Admin", "ADMIN@demo.com", "123", null, null);

    assertEquals(1L, dto.getId());
    assertTrue(dto.getIsAuditor()); // se activa por bootstrap
    assertEquals("admin@demo.com", dto.getEmail());
    verify(umpRepository).save(argThat(p ->
        p.getModule() == Module.USERS && p.getPermission() == ModulePermission.MUTATE
    ));
}


    @Test
    void setCoach_XOR_withAuditor_throws() {
        var u = new User();
        u.setId(10L);
        u.setIsAuditor(true); // ya es auditor
        when(userRepository.findById(10L)).thenReturn(Optional.of(u));

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(() -> SecurityUtils.requireMutate(Module.USERS)).thenAnswer(inv -> null);

            IllegalStateException ex = assertThrows(IllegalStateException.class,
                    () -> service.setCoach(10L, true));
            assertTrue(ex.getMessage().toLowerCase().contains("auditor y coach"));
        }
    }

    @Test
    void delete_removesPermsAndUser() {
        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(() -> SecurityUtils.requireMutate(Module.USERS)).thenAnswer(inv -> null);

            service.delete(77L);

            verify(umpRepository).deleteAllByUserId(77L);
            verify(userRepository).deleteById(77L);
        }
    }

    @Test
void pageUsers_empty_returnsEmptyInfo() {
    var page = new org.springframework.data.domain.PageImpl<com.tarea.models.User>(
            java.util.List.of(),
            org.springframework.data.domain.PageRequest.of(0, 10),
            0
    );

    when(userRepository.findAll(org.mockito.Mockito.any(org.springframework.data.domain.Pageable.class)))
            .thenReturn(page);

    var result = service.pageUsers(org.springframework.data.domain.PageRequest.of(0, 10));

    assertNotNull(result);
    assertEquals(0, result.getPageInfo().getTotalElements());
    assertTrue(result.getContent().isEmpty());
}


    @Test
    void setUserCoach_validatesCoachFlagAndSelfAssignment() {
        var user = new User(); user.setId(1L);
        var coach = new User(); coach.setId(2L); coach.setIsCoach(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(coach));

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(() -> SecurityUtils.requireMutate(Module.USERS)).thenAnswer(inv -> null);

            var dto = service.setUserCoach(1L, 2L);
            assertEquals(2L, dto.getAssignedCoachId());
            verify(userRepository, times(2)).findById(anyLong());
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void setUserCoach_throwsIfCoachNotCoach() {
        var user = new User(); user.setId(1L);
        var notCoach = new User(); notCoach.setId(3L); notCoach.setIsCoach(false);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.findById(3L)).thenReturn(Optional.of(notCoach));

        try (MockedStatic<SecurityUtils> mock = mockStatic(SecurityUtils.class)) {
            mock.when(() -> SecurityUtils.requireMutate(Module.USERS)).thenAnswer(inv -> null);
            assertThrows(IllegalStateException.class, () -> service.setUserCoach(1L, 3L));
        }
    }

    @Test
    void getById_throws_whenNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.getById(999L));
    }
}
