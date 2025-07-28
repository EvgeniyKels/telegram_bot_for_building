package kls.tgb.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ConstructionProjectDto (
        String name,
        BigDecimal totalBudget,
        LocalDate startDate,
        LocalDate endDate
) {}