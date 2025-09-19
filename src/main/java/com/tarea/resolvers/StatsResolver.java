package com.tarea.resolvers;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.services.StatsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StatsResolver {

    private final StatsService statsService;

    public StatsResolver(StatsService statsService) {
        this.statsService = statsService;
    }

    @QueryMapping
    public List<MonthlyCategoryStatDTO> monthlyCategoryStats(
            @Argument("year") int year,
            @Argument("month") Integer month,   // opcional (puede venir null)
            @Argument("userId") Long userId     // opcional (puede venir null)
    ) {
        return statsService.monthlyCategoryStats(year, month, userId);
    }
}
