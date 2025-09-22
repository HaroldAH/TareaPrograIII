package com.tarea.resolvers;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.services.StatsService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

/**
 * Reglas:
 * - userId == null  → no-staff: se asume "yo"; staff: se permite global.
 * - userId != yo    → requiere VIEW(PROGRESS). (La validación vive en el service)
 */
@Controller
public class StatsResolver {

    private final StatsService statsService;

    public StatsResolver(StatsService statsService) {
        this.statsService = statsService;
    }

    @QueryMapping
    public List<MonthlyCategoryStatDTO> monthlyCategoryStats(
            @Argument int year,
            @Argument Integer month,   // opcional
            @Argument Long userId      // opcional (null → ver reglas arriba)
    ) {
        return statsService.monthlyCategoryStats(year, month, userId);
    }
}
