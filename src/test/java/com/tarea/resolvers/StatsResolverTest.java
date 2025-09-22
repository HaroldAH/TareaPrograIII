package com.tarea.resolvers;

import com.tarea.dtos.MonthlyCategoryStatDTO;
import com.tarea.services.StatsService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsResolverTest {

    @Mock StatsService service;
    @InjectMocks StatsResolver resolver;

    @Test
    void monthlyCategoryStats_bridge_ok() {
        MonthlyCategoryStatDTO dto = new MonthlyCategoryStatDTO();
        dto.setYear(2025); dto.setMonth(9); dto.setCategory("MENTAL"); dto.setTotalCompletions(5);

        when(service.monthlyCategoryStats(2025, 9, 10L)).thenReturn(List.of(dto));

        var out = resolver.monthlyCategoryStats(2025, 9, 10L);
        assertThat(out).hasSize(1);
        assertThat(out.get(0).getCategory()).isEqualTo("MENTAL");
        verify(service).monthlyCategoryStats(2025, 9, 10L);
    }
}
