package com.tarea.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklist;

    public JwtAuthFilter(JwtService jwtService, TokenBlacklistService tokenBlacklist) {
        this.jwtService = jwtService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        final String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        if (!jwtService.validateToken(token) || tokenBlacklist.isBlacklisted(token)) {
            chain.doFilter(request, response);
            return;
        }

        Long userId = jwtService.getUserIdFromToken(token);
        String role = jwtService.getUserRoleFromToken(token); // p.ej. USER | ADMIN | COACH | AUDITOR

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
        var auth = new UsernamePasswordAuthenticationToken(userId, null, authorities);
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        chain.doFilter(request, response);
    }

    @Override
protected boolean shouldNotFilter(HttpServletRequest request) {
    String p = request.getRequestURI();
    return p.equals("/api/test/connection")
        || p.equals("/ping")
        || p.equals("/graphiql")
        || p.startsWith("/graphiql/")   // por si acaso
        || p.startsWith("/vendor/");    // assets de GraphiQL
    // OJO: /graphql ya NO va aquí
}
}
