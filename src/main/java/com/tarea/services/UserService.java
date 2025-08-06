package com.tarea.services;

import com.tarea.dtos.UserDTO;
import com.tarea.models.User;
import com.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        User entity = new User();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        
        // ⚠️ Si decides incluir password en el DTO, asegúrate de manejarlo correctamente
        if (dto.getPassword() != null) {
            entity.setPassword(dto.getPassword()); // Puedes encriptarlo aquí si deseas
        }

        User saved = userRepository.save(entity);
        return toDTO(saved);
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private UserDTO toDTO(User entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        // ❌ No devolver password en el DTO a menos que sea estrictamente necesario
        return dto;
    }
}
