package com.tarea.resolvers;

import com.tarea.dtos.UserDTO;
import com.tarea.resolvers.inputs.UserInput;
import com.tarea.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {

    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAll();
    }

    @PreAuthorize("hasAnyRole('ADMIN','COACH','AUDITOR')")
    @QueryMapping
    public UserDTO getUserById(@Argument Long id) {
        return userService.getById(id);
    }

    // Registro abierto; si quieres cerrarlo para admin, agrega @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public UserDTO createUser(@Argument UserInput input) {
        System.out.println(">>> Entrando a createUser con: " + input.getEmail());
        return userService.save(toDTO(input));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        userService.delete(id);
        return true;
    }

    private UserDTO toDTO(UserInput input) {
        UserDTO dto = new UserDTO();
        dto.setName(input.getName());
        dto.setEmail(input.getEmail());
        dto.setPassword(input.getPassword());
        dto.setRole(input.getRole());
        return dto;
    }
}
