package com.tarea.resolvers;

import com.tarea.dtos.UserDTO;
import com.tarea.resolvers.inputs.UserInput;
import com.tarea.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class UserResolver {

    
    private final UserService userService;

    public UserResolver(UserService userService) {
        this.userService = userService;
    }

    @QueryMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAll();
    }

    @QueryMapping
    public UserDTO getUserById(@Argument Long id) {
        return userService.getById(id);
    }

    @MutationMapping
    public UserDTO createUser(@Argument UserInput input) {
        return userService.save(toDTO(input)); // Asegúrate que este método exista en UserService
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        userService.delete(id);
        return true;
    }

    private UserDTO toDTO(UserInput input) {
        UserDTO dto = new UserDTO();
        dto.setId(input.getId());
        dto.setName(input.getName());
        dto.setEmail(input.getEmail());
        return dto;
    }
}
