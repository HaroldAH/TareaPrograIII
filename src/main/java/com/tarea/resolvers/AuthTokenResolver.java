package com.tarea.resolvers;

import com.tarea.dtos.AuthTokenDTO;
import com.tarea.resolvers.inputs.AuthTokenInput;
import com.tarea.services.AuthTokenService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.time.Instant;
import java.util.List;

@Controller
public class AuthTokenResolver {

    private final AuthTokenService authTokenService;

    public AuthTokenResolver(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @QueryMapping
    public List<AuthTokenDTO> getAllTokens() {
        return authTokenService.getAllTokens();
    }

    @QueryMapping
    public AuthTokenDTO getToken(@Argument String token) {
        return authTokenService.getToken(token);
    }

    @MutationMapping
    public AuthTokenDTO createToken(@Argument AuthTokenInput input) {
        return authTokenService.saveToken(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteToken(@Argument String token) {
        authTokenService.deleteToken(token);
        return true;
    }

    private AuthTokenDTO toDTO(AuthTokenInput input) {
        AuthTokenDTO dto = new AuthTokenDTO();
        dto.setToken(input.getToken());
        dto.setUserId(input.getUserId());
        dto.setExpiresAt(input.getExpiresAt() != null ? Instant.parse(input.getExpiresAt()) : null);
        return dto;
    }
}