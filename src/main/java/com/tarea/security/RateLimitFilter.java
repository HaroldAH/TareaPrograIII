package com.tarea.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
     
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
     
    private final Map<String, Bucket> graphqlBuckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();

        if (path.equals("/graphql") && "POST".equalsIgnoreCase(request.getMethod())) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            filterChain.doFilter(wrappedRequest, response);

            String body = new String(wrappedRequest.getContentAsByteArray(), wrappedRequest.getCharacterEncoding());
            if (body.contains("mutation") && body.contains("login")) {
                Bucket bucket = loginBuckets.computeIfAbsent(ip, k -> Bucket4j.builder()
                        .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofMinutes(5))))
                        .build());
                if (!bucket.tryConsume(1)) {
                    response.setStatus(429);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"errors\":[{\"message\":\"Too many login attempts. Try again later.\"}]}");
                    response.getWriter().flush();
                    return;
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
         
        return !(path.equals("/api/auth/login") || path.equals("/graphql"));
    }
}
