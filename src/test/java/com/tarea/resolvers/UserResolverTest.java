package com.tarea.resolvers;

import com.tarea.dtos.UserDTO;
import com.tarea.dtos.UserPageDTO;
import com.tarea.models.Module;
import com.tarea.models.ModulePermission;
import com.tarea.resolvers.inputs.ModulePermissionInput;
import com.tarea.resolvers.inputs.UserInput;
import com.tarea.security.SecurityUtils;
import com.tarea.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserResolverTest {
    
    @Mock private UserService userService;
    @InjectMocks private UserResolver resolver;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void getAllUsers_returnsList() {
        List<UserDTO> expectedList = List.of(new UserDTO());
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(userService.getAll()).thenReturn(expectedList);
            
            List<UserDTO> result = resolver.getAllUsers();
            assertEquals(expectedList, result);
        }
    }

    @Test
    void getUserById_returnsDTO() {
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(userService.getById(1L)).thenReturn(expectedDto);
            
            UserDTO result = resolver.getUserById(1L);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void createUser_returnsDTO() {
        UserInput input = new UserInput();
        input.setName("name");
        input.setEmail("email");
        input.setPassword("pass");
        input.setIsAuditor(false);
        input.setPermissions(List.of());
        input.setIsCoach(false);
        input.setAssignedCoachId(null);
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(userService.createUser(any(), any(), any(), any(), any(), any(), any())).thenReturn(expectedDto);
            
            UserDTO result = resolver.createUser(input);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void deleteUser_returnsTrue() {
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            doNothing().when(userService).delete(2L);
            
            boolean result = resolver.deleteUser(2L);
            assertTrue(result);
            verify(userService).delete(2L);
        }
    }

    @Test
    void setUserModulePermission_returnsDTO() {
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(userService.upsertPermission(3L, Module.USERS, ModulePermission.CONSULT)).thenReturn(expectedDto);
            
            UserDTO result = resolver.setUserModulePermission(3L, Module.USERS, ModulePermission.CONSULT);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void revokeUserModulePermission_returnsDTO() {
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(userService.revokePermission(4L, Module.USERS)).thenReturn(expectedDto);
            
            UserDTO result = resolver.revokeUserModulePermission(4L, Module.USERS);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void setUserPermissions_returnsDTO() {
        ModulePermissionInput input = new ModulePermissionInput(Module.USERS, ModulePermission.MUTATE);
        List<ModulePermissionInput> perms = List.of(input);
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(userService.upsertPermission(5L, Module.USERS, ModulePermission.MUTATE)).thenReturn(expectedDto);
            when(userService.getById(5L)).thenReturn(expectedDto);
            
            UserDTO result = resolver.setUserPermissions(5L, perms);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void setAuditor_returnsDTO() {
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(userService.setAuditor(6L, true)).thenReturn(expectedDto);
            
            UserDTO result = resolver.setAuditor(6L, true);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void setCoach_returnsDTO() {
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(userService.setCoach(7L, true)).thenReturn(expectedDto);
            
            UserDTO result = resolver.setCoach(7L, true);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void setUserCoach_returnsDTO() {
        UserDTO expectedDto = new UserDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireMutate(any())).thenAnswer(invocation -> null);
            when(userService.setUserCoach(8L, 9L)).thenReturn(expectedDto);
            
            UserDTO result = resolver.setUserCoach(8L, 9L);
            assertEquals(expectedDto, result);
        }
    }

    @Test
    void usersPage_returnsPageDTO() {
        UserPageDTO expectedDto = new UserPageDTO();
        
        try (MockedStatic<SecurityUtils> securityUtils = mockStatic(SecurityUtils.class)) {
            securityUtils.when(() -> SecurityUtils.requireView(any())).thenAnswer(invocation -> null);
            when(userService.pageUsers(any())).thenReturn(expectedDto);
            
            UserPageDTO result = resolver.usersPage(null);
            assertEquals(expectedDto, result);
        }
    }
}