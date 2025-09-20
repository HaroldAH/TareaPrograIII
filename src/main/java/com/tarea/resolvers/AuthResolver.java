package com.tarea.resolvers;

import com.tarea.models.ModulePermission;
import com.tarea.repositories.UserModulePermissionRepository;
import com.tarea.repositories.UserRepository;
import com.tarea.security.JwtService;
import com.tarea.security.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthResolver {

  private final UserRepository userRepo;
  private final UserModulePermissionRepository permRepo;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final TokenBlacklistService blacklist;

  @MutationMapping
  public String login(@Argument String email, @Argument String password) {
    var user = userRepo.findByEmail(email)
        .orElseThrow(() -> new RuntimeException("Bad credentials"));

    if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
      throw new RuntimeException("Bad credentials");
    }

    List<String> auths = new ArrayList<>();
    if (Boolean.TRUE.equals(user.getIsAuditor())) {
      auths.add("AUDITOR");
    }

    permRepo.findAllByUserId(user.getId()).forEach(p -> {
      String suffix = (p.getPermission() == ModulePermission.MUTATE) ? ":RW" : ":R";
      auths.add("MOD:" + p.getModule().name() + suffix);
    });

    return jwtService.generateToken(user.getId(), auths);
  }

  @MutationMapping
  public Boolean logout(@Argument String token) {
    blacklist.blacklist(token);
    return true;
  }
}
