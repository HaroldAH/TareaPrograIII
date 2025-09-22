package com.tarea.security;

import com.tarea.models.Module;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;    
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {
  private SecurityUtils(){}

 

  public static void requireView(Module m) {
    requireAny("AUDITOR", "MOD:" + m + ":R", "MOD:" + m + ":RW");
  }

  public static void requireMutate(Module m) {
    requireAny("MOD:" + m + ":RW");
  }

 
  public static void requireSelfOrView(Long targetUserId, Module m) {
    Long me = userId();
    if (!me.equals(targetUserId)) {
      requireView(m);
    }
  }

 
  public static void requireSelfOrMutate(Long targetUserId, Module m) {
    Long me = userId();
    if (!me.equals(targetUserId)) {
      requireMutate(m);
    }
  }

 
  public static boolean canView(Module m) {
    return hasAny("AUDITOR", "MOD:" + m + ":R", "MOD:" + m + ":RW");
  }

  public static boolean canMutate(Module m) {
    return hasAny("MOD:" + m + ":RW");
  }

  private static void requireAny(String... needed) {
    if (!hasAny(needed)) {
      throw new AccessDeniedException("Forbidden");
    }
  }

  private static boolean hasAny(String... needed) {
    var ctx = SecurityContextHolder.getContext();
    if (ctx == null || ctx.getAuthentication() == null) {
      throw new AccessDeniedException("Unauthorized");
    }
    var auths = ctx.getAuthentication().getAuthorities();
    for (String n : needed) {
      if (auths.contains(new SimpleGrantedAuthority(n))) return true;
    }
    return false;
  }

 

 
  public static boolean isAuditor() {
    var ctx = SecurityContextHolder.getContext();
    if (ctx == null || ctx.getAuthentication() == null) {
      throw new AccessDeniedException("Unauthorized");
    }
    for (GrantedAuthority a : ctx.getAuthentication().getAuthorities()) {
      if ("AUDITOR".equals(a.getAuthority())) return true;
    }
    return false;
  }

 
  public static void forbidAuditorWrites() {
    if (isAuditor()) {
      throw new AccessDeniedException("Forbidden: auditors are read-only");
    }
  }

 

  public static Long userId() {
    Authentication auth = SecurityContextHolder.getContext() != null
        ? SecurityContextHolder.getContext().getAuthentication()
        : null;

    if (auth == null || auth.getPrincipal() == null) {
      throw new AccessDeniedException("Unauthorized");
    }

    Object p = auth.getPrincipal();

    if (p instanceof Long l) return l;
    if (p instanceof Integer i) return i.longValue();
    if (p instanceof String s) {
      try { return Long.parseLong(s); }
      catch (NumberFormatException e) {  }
    }
    if (p instanceof UserDetails ud) {
      String u = ud.getUsername();
      try { return Long.parseLong(u); }
      catch (NumberFormatException e) {  }
    }
    if (p instanceof java.security.Principal pr) {
      try { return Long.parseLong(pr.getName()); }
      catch (NumberFormatException e) {  }
    }

     
    throw new AccessDeniedException("Unauthorized");
  }
}
