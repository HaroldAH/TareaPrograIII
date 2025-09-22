package com.tarea.config;

import com.tarea.security.JwtAuthFilter;
import com.tarea.security.RateLimitFilter;
import com.tarea.security.InputSanitizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;   // <-- importa esto
import org.springframework.security.crypto.password.PasswordEncoder;    // <-- y esto

@EnableMethodSecurity(prePostEnabled = true) // puedes dejarlo, aunque ya no uses @PreAuthorize
@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, RateLimitFilter rateLimitFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    // ⬇️ AÑADE ESTE BEAN
    @Bean

public PasswordEncoder passwordEncoder() {
    return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
}


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/test/**", "/ping").permitAll()
                .requestMatchers("/graphiql", "/graphiql/**", "/vendor/**").permitAll()
                // Dejamos /graphql como permitAll porque el login/logout también entran por aquí.
                // Los resolvers protegidos fallarán por SecurityUtils si no hay authorities en el token.
                .requestMatchers("/graphql").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new InputSanitizationFilter(), JwtAuthFilter.class);

        return http.build();
    }
}
