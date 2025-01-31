package dev.kaly7.fingest.dto;

import dev.kaly7.fingest.entities.Category;
import dev.kaly7.fingest.entities.Expense;
import dev.kaly7.fingest.entities.money.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record ExpenseInputDto(
        String message,
        @NotNull @Valid Money amount,
        @NotNull @Valid Category category
) {
    public Expense toExpense() {
        return Expense.builder()
                .amount(amount)
                .category(category)
                .message(message)
                .build();
    }
}
