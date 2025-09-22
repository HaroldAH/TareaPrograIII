package com.tarea.resolvers;

import com.tarea.models.ModulePermission;
import com.tarea.models.User;
import com.tarea.repositories.UserModulePermissionRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.JwtService;
import com.tarea.security.TokenBlacklistService;
import com.tarea.services.AccountService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthResolverTest {
    @Mock
    private UserRepository userRepo;
    @Mock
    private UserModulePermissionRepository permRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private TokenBlacklistService blacklist;
    @Mock
    private AccountService accountService;

    @InjectMocks
    private AuthResolver authResolver;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        authResolver = new AuthResolver(userRepo, permRepo, passwordEncoder, jwtService, blacklist, accountService);
    }

    @Test
    void login_success_auditor() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");
        user.setPassword("encoded");
        user.setIsAuditor(true);
        when(userRepo.findByEmail("test@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);
        when(permRepo.findAllByUserId(1L)).thenReturn(List.of());
        when(jwtService.generateToken(eq(1L), anyList())).thenReturn("token");

        String token = authResolver.login("test@test.com", "pass");
        assertEquals("token", token);
    }

    @Test
    void login_success_with_permissions() {
        User user = new User();
        user.setId(2L);
        user.setEmail("user@test.com");
        user.setPassword("encoded");
        user.setIsAuditor(false);
        when(userRepo.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "encoded")).thenReturn(true);
        var perm = mock(com.tarea.models.UserModulePermission.class);
        when(perm.getPermission()).thenReturn(ModulePermission.MUTATE);
        when(perm.getModule()).thenReturn(com.tarea.models.Module.PROGRESS);
        when(permRepo.findAllByUserId(2L)).thenReturn(List.of(perm));
        when(jwtService.generateToken(eq(2L), anyList())).thenReturn("token2");

        String token = authResolver.login("user@test.com", "pass");
        assertEquals("token2", token);
    }

    @Test
    void login_bad_credentials_user_not_found() {
        when(userRepo.findByEmail("no@no.com")).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> authResolver.login("no@no.com", "pass"));
    }

    @Test
    void login_bad_credentials_wrong_password() {
        User user = new User();
        user.setId(3L);
        user.setEmail("fail@test.com");
        user.setPassword("encoded");
        when(userRepo.findByEmail("fail@test.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("bad", "encoded")).thenReturn(false);
        assertThrows(RuntimeException.class, () -> authResolver.login("fail@test.com", "bad"));
    }

    @Test
    void login_bad_credentials_null_password() {
        User user = new User();
        user.setId(4L);
        user.setEmail("null@test.com");
        user.setPassword(null);
        when(userRepo.findByEmail("null@test.com")).thenReturn(Optional.of(user));
        assertThrows(RuntimeException.class, () -> authResolver.login("null@test.com", "pass"));
    }

    @Test
    void logout_blacklists_token() {
        String token = "sometoken";
        Boolean result = authResolver.logout(token);
        verify(blacklist).blacklist(token);
        assertTrue(result);
    }
}
