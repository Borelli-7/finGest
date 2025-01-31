package dev.kaly7.fingest.exceptions;

public class ExpenseNotFoundException extends NotFoundException {
    public ExpenseNotFoundException(Integer id) {
        super("Expense with id " + id + " was not found.", "Check if provided id is correct and try again.");
    }
}
