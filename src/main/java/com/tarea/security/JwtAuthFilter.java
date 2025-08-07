package com.tarea.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.util.Collections;

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
                                FilterChain filterChain)
        throws ServletException, IOException {

    final String header = request.getHeader("Authorization");
    final String token;

    // Permitir createUser y login sin token
    if (request.getRequestURI().equals("/graphql")) {
        String body = request.getReader().lines().reduce("", (acc, line) -> acc + line);
        if (body.contains("createUser") || body.contains("login")) {
            filterChain.doFilter(request, response);
            return;
        }
    }

    if (header == null || !header.startsWith("Bearer ")) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    token = header.substring(7);

    if (!jwtService.validateToken(token) || tokenBlacklist.isBlacklisted(token)) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
    }

    Long userId = jwtService.getUserIdFromToken(token);
    String role = jwtService.getUserRoleFromToken(token);

    var auth = new UsernamePasswordAuthenticationToken(
            userId, null, Collections.emptyList()
    );

    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
    SecurityContextHolder.getContext().setAuthentication(auth);

    filterChain.doFilter(request, response);
}

}
