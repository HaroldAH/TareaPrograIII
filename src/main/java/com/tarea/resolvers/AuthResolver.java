package com.tarea.resolvers;

import com.tarea.models.User;
import com.tarea.security.JwtService;
import com.tarea.security.TokenBlacklistService;
import com.repositories.UserRepository;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Controller
public class AuthResolver {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklist;

    public AuthResolver(UserRepository userRepository, JwtService jwtService, TokenBlacklistService tokenBlacklist) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.tokenBlacklist = tokenBlacklist;
    }

    @MutationMapping
    public String login(@Argument String email, @Argument String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtService.generateToken(user.getId(), user.getRole());
    }

    @MutationMapping
    public Boolean logout(@Argument String token) {
        tokenBlacklist.blacklist(token);
        return true;
    }
}
