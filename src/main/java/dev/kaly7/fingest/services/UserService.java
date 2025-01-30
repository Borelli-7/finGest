package dev.kaly7.fingest.services;

import dev.kaly7.fingest.dto.ExpenseInputDto;
import dev.kaly7.fingest.dto.SummaryDto;
import dev.kaly7.fingest.dto.UserDto;
import dev.kaly7.fingest.dto.WalletDto;
import dev.kaly7.fingest.entities.DateRange;
import dev.kaly7.fingest.entities.Expense;
import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserDto> getUsers();

    void updateUser(String login, String field, Map<String, Object> value);

    List<WalletDto> getWallets(String login);

    Integer addWallet(String login, @Valid WalletDto walletDto);

    SummaryDto getSummary(String login, Integer id, DateRange dateRange);

    List<Expense> getExpenses(String login, Integer id, DateRange dateRange);

    Expense getHighestExpense(String login, Integer id, DateRange dateRange);

    Integer addExpense(String login, Integer id, @Valid ExpenseInputDto expenseInputDto);

    void deleteExpense(String login, Integer walletId, Integer expenseId);

    Map<String, BigDecimal> getCountedCategories(String login, Integer id, DateRange dateRange);
}
