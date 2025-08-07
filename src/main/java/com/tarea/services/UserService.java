package com.tarea.services;

import com.tarea.dtos.UserDTO;
import com.tarea.models.User;
import com.tarea.repositories.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElse(null);
    }

    public UserDTO save(UserDTO dto) {
    try {
        System.out.println(">>> Guardando usuario con email: " + dto.getEmail());

        User entity = new User();
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());

        if (dto.getPassword() != null) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            entity.setPassword(encoder.encode(dto.getPassword()));
        }

        entity.setRole(dto.getRole());

        User saved = userRepository.save(entity);
        System.out.println(">>> Usuario guardado con ID: " + saved.getId());

        return toDTO(saved);

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("Error al guardar usuario: " + e.getMessage(), e);
    }
}

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO toDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setRole(entity.getRole());
        return dto;
    }
}
