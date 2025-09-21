package com.tarea.security;

import com.tarea.models.Module;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {
  private SecurityUtils(){}

  /* ================== Permisos por módulo ================== */

  public static void requireView(Module m) {
    requireAny("AUDITOR", "MOD:" + m + ":R", "MOD:" + m + ":RW");
  }

  public static void requireMutate(Module m) {
    requireAny("MOD:" + m + ":RW");
  }

  private static void requireAny(String... needed) {
    var ctx = SecurityContextHolder.getContext();
    if (ctx == null || ctx.getAuthentication() == null) {
      throw new AccessDeniedException("Unauthorized");
    }
    var auths = ctx.getAuthentication().getAuthorities();
    for (String n : needed) {
      if (auths.contains(new SimpleGrantedAuthority(n))) return;
    }
    throw new AccessDeniedException("Forbidden");
  }

  /* ================== Identidad del usuario ================== */

  /**
   * Devuelve el ID del usuario autenticado tomando el principal del contexto.
   * Soporta principal como Long, Integer o String (numérico), y también
   * UserDetails/Principal cuyo "username/name" sea numérico.
   *
   * Lanza AccessDeniedException("Unauthorized") si no hay sesión o si no
   * puede obtener un ID numérico.
   */
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
      catch (NumberFormatException e) { /* fallthrough */ }
    }
    if (p instanceof UserDetails ud) {
      String u = ud.getUsername();
      try { return Long.parseLong(u); }
      catch (NumberFormatException e) { /* fallthrough */ }
    }
    if (p instanceof java.security.Principal pr) {
      try { return Long.parseLong(pr.getName()); }
      catch (NumberFormatException e) { /* fallthrough */ }
    }

    // Si llegamos aquí, tu JwtAuthFilter no está poniendo el userId como principal.
    // Ajusta el filtro para setear el ID (String/Long) en el Authentication#setPrincipal.
    throw new AccessDeniedException("Unauthorized");
  }
}
