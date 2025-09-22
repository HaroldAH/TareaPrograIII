package com.tarea.resolvers;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.services.StatsService;
import java.util.List;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class StatsResolver {

    private final StatsService statsService;

    public StatsResolver(StatsService statsService) {
        this.statsService = statsService;
    }

    @QueryMapping
    public List<MonthlyCategoryStatDTO> monthlyCategoryStats(
            @Argument int year,
            @Argument Integer month,    
            @Argument Long userId       
    ) {
        return statsService.monthlyCategoryStats(year, month, userId);
    }
}
