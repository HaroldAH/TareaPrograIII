package com.tarea.security;

import com.tarea.models.Module;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {
  private SecurityUtils(){}

  public static void requireView(Module m) {
    requireAny("AUDITOR", "MOD:"+m+":R", "MOD:"+m+":RW");
  }
  public static void requireMutate(Module m) {
    requireAny("MOD:"+m+":RW");
  }
  private static void requireAny(String... needed) {
    var ctx = SecurityContextHolder.getContext();
    if (ctx.getAuthentication()==null) throw new AccessDeniedException("Unauthorized");
    var auths = ctx.getAuthentication().getAuthorities();
    for (String n: needed) if (auths.contains(new SimpleGrantedAuthority(n))) return;
    throw new AccessDeniedException("Forbidden");
  }
}
