package dev.kaly7.fingest.common.validation;

import dev.kaly7.fingest.entities.*;


import java.util.Objects;
import java.util.function.Predicate;

public class Predicates {

    private static final String TRANSFER_CATEGORY = "transfer";

    // Functional and reusable predicates
    static final Predicate<Expense> IS_PROFIT = expense -> expense.getCategory().getProfit();
    public static final Predicate<Expense> IS_NOT_PROFIT = IS_PROFIT.negate();
    static final Predicate<Expense> IS_NOT_TRANSFER = expense ->
            !TRANSFER_CATEGORY.equalsIgnoreCase(expense.getCategory().getName());
    static final Predicate<Expense> IS_ELIGIBLE_EXPENSE = IS_NOT_TRANSFER.and(IS_NOT_PROFIT);

    // Expense-specific predicates
    public static Predicate<Expense> isIn(DateRange dateRange) {
        return expense -> dateRange.containsDate(expense.getDate());
    }

    static Predicate<Expense> matchesCategoryOf(Budget budget) {
        return expense -> Objects.equals(expense.getCategory(), budget.getCategory());
    }

    static Predicate<Expense> isIncludedIn(Budget budget) {
        return matchesCategoryOf(budget).and(isIn(budget.getDateRange()));
    }

    static Predicate<Expense> hasId(Integer id) {
        return expense -> Objects.equals(expense.getId(), id);
    }

    // Budget-specific predicates
    static Predicate<Budget> isIn(DateRange start, DateRange end) {
        return budget -> start.containsDate(budget.getDateRange().getStart()) &&
                end.containsDate(budget.getDateRange().getEnd());
    }

    // Wallet-specific predicates
    static Predicate<Wallet> containsExpenseWithId(Integer id) {
        return wallet -> wallet.getExpenses().stream().anyMatch(hasId(id));
    }
}
