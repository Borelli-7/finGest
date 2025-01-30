package dev.kaly7.fingest.dto;

import dev.kaly7.fingest.entities.Budget;
import dev.kaly7.fingest.entities.Category;
import dev.kaly7.fingest.entities.DateRange;
import dev.kaly7.fingest.entities.money.Money;

public record BudgetOutputDto(
        Integer id,
        Category category,
        Money total,
        Money current,
        DateRange dateRange
) {
    public static BudgetOutputDto fromBudget(Budget budget, Money current) {
        return new BudgetOutputDto(
                budget.getId(),
                budget.getCategory(),
                budget.getTotal(),
                current,
                budget.getDateRange()
        );
    }
}
