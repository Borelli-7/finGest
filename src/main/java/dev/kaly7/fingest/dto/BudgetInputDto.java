package dev.kaly7.fingest.dto;

import dev.kaly7.fingest.entities.Budget;
import dev.kaly7.fingest.entities.Category;
import dev.kaly7.fingest.entities.DateRange;
import dev.kaly7.fingest.entities.money.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record BudgetInputDto(
        @NotNull @Valid Category category,
        @NotNull @Valid Money total,
        @NotNull @Valid DateRange dateRange
) {
    public Budget toBudget() {
        return Budget.builder()
                .category(category)
                .total(total)
                .dateRange(dateRange)
                .build();
    }
}
