 
package com.tarea.services;

import com.tarea.dtos.UserDTO;
import com.tarea.dtos.UserPermissionDTO;
import com.tarea.models.Module;
import com.tarea.models.ModulePermission;
import com.tarea.models.PasswordResetToken;
import com.tarea.models.User;
import com.tarea.models.UserModulePermission;
import com.tarea.repositories.PasswordResetTokenRepository;
import com.tarea.repositories.UserModulePermissionRepository;
import com.tarea.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class AccountService {

    private final UserRepository userRepo;
    private final UserModulePermissionRepository umpRepo;
    private final PasswordResetTokenRepository prtRepo;
    private final PasswordEncoder encoder;

    public AccountService(UserRepository userRepo,
                          UserModulePermissionRepository umpRepo,
                          PasswordResetTokenRepository prtRepo,
                          PasswordEncoder encoder) {
        this.userRepo = userRepo;
        this.umpRepo = umpRepo;
        this.prtRepo = prtRepo;
        this.encoder = encoder;
    }

    @Value("${app.frontend.resetBaseUrl:http://localhost:5173/reset-password}")
    private String resetBaseUrl;

    @Value("${app.reset.tokenMinutes:30}")
    private int tokenMinutes;


    @Transactional
    public UserDTO registerPublic(String name, String email, String rawPassword) {
        String normalizedEmail = email == null ? null : email.trim().toLowerCase(Locale.ROOT);
        userRepo.findByEmail(normalizedEmail).ifPresent(u -> {
            throw new IllegalArgumentException("Email ya registrado");
        });

        User u = new User();
        u.setName(name);
        u.setEmail(normalizedEmail);
        u.setPassword(encoder.encode(rawPassword));
        u.setIsAuditor(false);
         

        u = userRepo.save(u);

         
        upsertPermission(u, Module.HABITS, ModulePermission.CONSULT);
        upsertPermission(u, Module.REMINDERS, ModulePermission.CONSULT);

        return toDTO(u, loadPerms(u.getId()));
    }

 

    @Transactional
    public boolean requestPasswordReset(String email) {
        if (email == null) return true;  
        var opt = userRepo.findByEmail(email.trim().toLowerCase(Locale.ROOT));
        if (opt.isEmpty()) return true;  

        User u = opt.get();

         
        prtRepo.deleteByUser_Id(u.getId());

         
        PasswordResetToken t = new PasswordResetToken();
        t.setUser(u);
        t.setToken(generateToken());
        t.setExpiresAt(Instant.now().plus(tokenMinutes, ChronoUnit.MINUTES));
        prtRepo.save(t);

        String link = resetBaseUrl + "?token=" + t.getToken();

         
        System.out.println("[RESET LINK] " + link);

        return true;
    }

    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        var t = prtRepo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (t.isUsed()) throw new IllegalStateException("Token ya utilizado");
        if (Instant.now().isAfter(t.getExpiresAt())) throw new IllegalStateException("Token expirado");

        User u = t.getUser();
        u.setPassword(encoder.encode(newPassword));
        userRepo.save(u);

        t.setUsed(true);
        prtRepo.save(t);

         
        prtRepo.deleteByExpiresAtBefore(Instant.now().minus(1, ChronoUnit.DAYS));
        return true;
    }

    @Transactional
    public boolean changeMyPassword(String oldPassword, String newPassword) {
        Long me = currentUserId()
                .orElseThrow(() -> new IllegalStateException("No autenticado"));
        User u = userRepo.findById(me).orElseThrow();
        if (u.getPassword() != null && !encoder.matches(oldPassword, u.getPassword())) {
            throw new IllegalArgumentException("Contraseña actual incorrecta");
        }
        u.setPassword(encoder.encode(newPassword));
        userRepo.save(u);
         
        return true;
    }

 

    private Optional<Long> currentUserId() {
        Authentication a = SecurityContextHolder.getContext().getAuthentication();
        if (a==null || a.getName()==null) return Optional.empty();
        try { return Optional.of(Long.parseLong(a.getName())); }
        catch (NumberFormatException e) { return Optional.empty(); }
    }

    private static String generateToken() {
        byte[] bytes = new byte[24];  
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private void upsertPermission(User user, Module module, ModulePermission permission) {
        var existing = umpRepo.findByUserIdAndModule(user.getId(), module);
        if (existing.isPresent()) {
            var e = existing.get();
            e.setPermission(permission);
            umpRepo.save(e);
        } else {
            umpRepo.save(new UserModulePermission(user, module, permission));
        }
    }

    @Transactional(readOnly = true)
    private List<UserPermissionDTO> loadPerms(Long userId) {
        return umpRepo.findAllByUserId(userId).stream().map(p -> {
            var dto = new UserPermissionDTO();
            dto.setModule(p.getModule());
            dto.setPermission(p.getPermission());
            return dto;
        }).toList();
    }

    private UserDTO toDTO(User u, List<UserPermissionDTO> perms) {
        var dto = new UserDTO();
        dto.setId(u.getId());
        dto.setName(u.getName());
        dto.setEmail(u.getEmail());
        dto.setIsAuditor(Boolean.TRUE.equals(u.getIsAuditor()));
        dto.setPermissions(perms);
        return dto;
    }
}
