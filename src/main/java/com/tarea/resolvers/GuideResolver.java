package com.tarea.resolvers;

import com.tarea.dtos.GuideDTO;
import com.tarea.dtos.GuideDetailDTO;
import com.tarea.resolvers.inputs.GuideInput;
import com.tarea.services.GuideService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GuideResolver {

    private final GuideService guideService;

    public GuideResolver(GuideService guideService) {
        this.guideService = guideService;
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public List<GuideDTO> getAllGuides() {
        return guideService.getAll();
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public GuideDTO getGuideById(@Argument Long id) {
        return guideService.getById(id);
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public GuideDTO createGuide(@Argument GuideInput input, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        Long me = Long.valueOf(auth.getName());

        if (!isStaff) {
            // UPDATE: verificar dueño de la guía existente
            if (input.getId() != null) {
                GuideDTO existing = guideService.getById(input.getId());
                if (existing == null) {
                    throw new IllegalArgumentException("Guía no encontrada: " + input.getId());
                }
                if (!me.equals(existing.getUserId())) {
                    throw new org.springframework.security.access.AccessDeniedException("Forbidden");
                }
            }
            // CREATE: forzar/validar userId = yo
            if (input.getUserId() == null) {
                input.setUserId(me);
            } else if (!me.equals(input.getUserId())) {
                throw new org.springframework.security.access.AccessDeniedException("Forbidden");
            }
        }
        return guideService.save(toDTO(input));
    }

    @PreAuthorize("isAuthenticated() and !hasRole('AUDITOR')")
    @MutationMapping
    public Boolean deleteGuide(@Argument Long id, Authentication auth) {
        boolean isStaff = auth.getAuthorities().stream().anyMatch(a ->
                a.getAuthority().equals("ROLE_ADMIN") ||
                a.getAuthority().equals("ROLE_COACH") ||
                a.getAuthority().equals("ROLE_AUDITOR"));
        if (!isStaff) {
            GuideDTO g = guideService.getById(id);
            if (g == null) {
                throw new IllegalArgumentException("Guía no encontrada: " + id);
            }
            if (!String.valueOf(g.getUserId()).equals(auth.getName())) {
                throw new org.springframework.security.access.AccessDeniedException("Forbidden");
            }
        }
        guideService.delete(id);
        return true;
    }

    // Filtros
    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public List<GuideDTO> getGuidesByCategory(@Argument String category) {
        return guideService.getByCategory(category);
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public List<GuideDTO> getGuidesByUser(@Argument Long userId) {
        return guideService.getByUserId(userId);
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public List<GuideDTO> getGuidesByCategoryAndUser(@Argument String category, @Argument Long userId) {
        return guideService.getByCategoryAndUserId(category, userId);
    }

    @PreAuthorize("hasAnyRole('USER','COACH','ADMIN','AUDITOR')")
    @QueryMapping
    public GuideDetailDTO getGuideDetail(@Argument Long id) {
        return guideService.getGuideDetail(id);
    }

    private GuideDTO toDTO(GuideInput input) {
        GuideDTO dto = new GuideDTO();
        dto.setId(input.getId());
        dto.setTitle(input.getTitle());
        dto.setContent(input.getContent());
        dto.setCategory(input.getCategory());
        dto.setUserId(input.getUserId());
        return dto;
    }
}
