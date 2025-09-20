package com.tarea.resolvers;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.models.Module;
import com.tarea.services.StatsService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.tarea.security.SecurityUtils.requireView;

@Controller
public class StatsResolver {

    private final StatsService statsService;

    public StatsResolver(StatsService statsService) {
        this.statsService = statsService;
    }

    @QueryMapping
    public List<MonthlyCategoryStatDTO> monthlyCategoryStats(
            @Argument("year") int year,
            @Argument("month") Integer month,   // opcional
            @Argument("userId") Long userId,    // opcional
            Authentication auth                  // no afecta el schema
    ) {
        requireView(Module.PROGRESS);

        Long uid = userId;
        if (uid == null && auth != null) {
            try { uid = Long.valueOf(auth.getName()); } catch (NumberFormatException ignored) {}
        }

        return statsService.monthlyCategoryStats(year, month, uid);
    }
}
