package com.tarea.resolvers;

import com.tarea.dtos.GuideDTO;
import com.tarea.dtos.GuideDetailDTO;
import com.tarea.models.Module;
import com.tarea.resolvers.inputs.GuideInput;
import com.tarea.services.GuideService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireMutate;
import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class GuideResolver {

    private final GuideService guideService;

    public GuideResolver(GuideService guideService) {
        this.guideService = guideService;
    }

 

    @QueryMapping
    public List<GuideDTO> getAllGuides() {
        requireView(Module.GUIDES);
        return guideService.getAll();
    }

    @QueryMapping
    public GuideDTO getGuideById(@Argument Long id) {
        requireView(Module.GUIDES);
        return guideService.getById(id);
    }

    @QueryMapping
    public List<GuideDTO> getGuidesByCategory(@Argument String category) {
        requireView(Module.GUIDES);
        return guideService.getByCategory(category);
    }

    @QueryMapping
    public List<GuideDTO> getGuidesByUser(@Argument Long userId) {
        requireView(Module.GUIDES);
        return guideService.getByUserId(userId);
    }

    @QueryMapping
    public List<GuideDTO> getGuidesByCategoryAndUser(@Argument String category, @Argument Long userId) {
        requireView(Module.GUIDES);
        return guideService.getByCategoryAndUserId(category, userId);
    }

    @QueryMapping
    public GuideDetailDTO getGuideDetail(@Argument Long id) {
        requireView(Module.GUIDES);
        return guideService.getGuideDetail(id);
    }

 

    @MutationMapping
    public GuideDTO createGuide(@Argument GuideInput input, Authentication auth) {
        requireMutate(Module.GUIDES);
         
        if (input.getUserId() == null && auth != null) {
            try { input.setUserId(Long.valueOf(auth.getName())); } catch (NumberFormatException ignored) {}
        }
        return guideService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteGuide(@Argument Long id) {
        requireMutate(Module.GUIDES);
        guideService.delete(id);
        return true;
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
