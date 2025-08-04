package com.tarea.resolvers;

import com.tarea.dtos.GuideDTO;
import com.tarea.resolvers.inputs.GuideInput;
import com.tarea.services.GuideService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;

@Controller
public class GuideResolver {

    private final GuideService guideService;

    public GuideResolver(GuideService guideService) {
        this.guideService = guideService;
    }

    @QueryMapping
    public List<GuideDTO> getAllGuides() {
        return guideService.getAll();
    }

    @QueryMapping
    public GuideDTO getGuideById(@Argument Long id) {
        return guideService.getById(id);
    }

    @MutationMapping
    public GuideDTO createGuide(@Argument GuideInput input) {
        return guideService.save(toDTO(input));
    }

    @MutationMapping
    public Boolean deleteGuide(@Argument Long id) {
        guideService.delete(id);
        return true;
    }

    private GuideDTO toDTO(GuideInput input) {
        GuideDTO dto = new GuideDTO();
        dto.setId(input.getId());
        dto.setTitle(input.getTitle());
        dto.setContent(input.getContent());
        dto.setCategory(input.getCategory());
        return dto;
    }
}