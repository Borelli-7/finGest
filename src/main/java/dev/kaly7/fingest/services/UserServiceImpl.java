package dev.kaly7.fingest.services;

import dev.kaly7.fingest.common.validation.Predicates;
import dev.kaly7.fingest.db.repositories.BudgetRepo;
import dev.kaly7.fingest.db.repositories.ExpenseRepo;
import dev.kaly7.fingest.db.repositories.UserRepo;
import dev.kaly7.fingest.db.repositories.WalletRepo;
import dev.kaly7.fingest.dto.*;
import dev.kaly7.fingest.entities.*;
import dev.kaly7.fingest.entities.money.Money;
import dev.kaly7.fingest.exceptions.ExpenseNotFoundException;
import dev.kaly7.fingest.exceptions.WalletNotFoundException;
import org.apache.commons.beanutils.PropertyUtils;
import dev.kaly7.fingest.exceptions.UpdateFieldException;
import dev.kaly7.fingest.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

@Service
public class UserServiceImpl implements UserService {

    private static final String SUMMARY_WALLET_NAME = "summary";

    private final UserRepo userRepo;
    private final WalletRepo walletRepo;
    private final ExpenseRepo expenseRepo;
    private final BudgetRepo budgetRepo;

    public UserServiceImpl(UserRepo userRepo, WalletRepo walletRepo, ExpenseRepo expenseRepo, BudgetRepo budgetRepo) {
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
        this.expenseRepo = expenseRepo;
        this.budgetRepo = budgetRepo;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepo.findAll()
                        .stream()
                        .map(UserDto::fromUser)
                        .toList();
    }

    @Override
    public void updateUser(String login, String field, Map<String, Object> value) {

        Optional.ofNullable(value.get(field))
                .orElseThrow(() -> new UpdateFieldException(
                        "Requested field value is missing.",
                        "Ensure the provided field and value are correct."
                ));

        getUser(login)
                .map(user -> {
                    try {
                        PropertyUtils.setProperty(user, field, value.get(field));
                        return user;
                    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                        throw new UpdateFieldException(
                                "Requested field cannot be updated.",
                                "Validate entered data and try again."
                        );
                    }
                })
                .ifPresent(userRepo::save);
    }

    @Override
    public List<WalletDto> getWallets(String login) {
        final var user = getUser(login);

        if (user.isPresent())
            return Stream.concat(
                        Stream.of(getSummaryWallet(user)),
                        Optional.ofNullable(walletRepo.findByUserOrderByIdAsc(user.get()))
                                .stream()
                                .flatMap(Collection::stream)
                                .map(WalletDto::fromWallet)
                ).toList();
        else
            throw new UserNotFoundException(login);
    }

    @Override
    public Integer addWallet(String login, WalletDto walletDto) {

        return Optional.ofNullable(login)
                .flatMap(this::getUser)
                .map(user -> {
                    var wallet = walletDto.toWallet();
                    var id = walletRepo.save(wallet).getId();
                    user.getWallets().add(wallet);
                    userRepo.save(user);
                    return id;
                })
                .orElseThrow(() -> new UserNotFoundException(login));
    }

    @Override
    public SummaryDto getSummary(String login, Integer id, DateRange dateRange) {
        return calculateSummary(getExpenses(login, id, dateRange));
    }

    @Override
    public List<Expense> getExpenses(String login, Integer id, DateRange dateRange) {

        return getUser(login)
                .map(user -> {
                    if (id == 0) {
                        return getAllExpenses(user, dateRange);
                    } else {
                        return getExpensesFromWallet(user, id, dateRange);
                    }
                }).orElseThrow(() -> new IllegalArgumentException("User not found for login: " + login));
    }

    @Override
    public Expense getHighestExpense(String login, Integer id, DateRange dateRange) {

        return getExpenses(login, id, dateRange)
                .stream()
                .filter(Predicates.IS_NOT_PROFIT)
                .max(Comparator.comparing(Expense::getAmount))
                .orElseThrow(() -> new IllegalArgumentException("No expenses found for user: " + login));
    }

    @Override
    public Integer addExpense(String login, Integer id, ExpenseInputDto expenseInputDto) {

        return getUser(login)
                            .map(user -> getWallet(id, user))
                            .map(wallet -> {
                                final var expense = expenseInputDto.toExpense();
                                expense.setDate(LocalDate.now());
                                final var expenseId = expenseRepo.save(expense).getId();

                                wallet.getExpenses().add(expense);
                                wallet.setAmount(getNewAmountAfterAdd(expenseInputDto, wallet));
                                walletRepo.save(wallet);

                                return expenseId;
                            })
                            .orElseThrow(() -> new UserNotFoundException("User not found with login: " + login));
    }

    @Override
    public void deleteExpense(String login, Integer walletId, Integer expenseId) {
        getUser(login)
                .map(user -> walletId == 0 ? getWalletByExpenseId(expenseId, user) : getWallet(walletId, user))
                .map(wallet -> {
                    wallet.getExpenses()
                            .stream()
                            .filter(Predicates.hasId(expenseId))
                            .findFirst()
                            .ifPresentOrElse(
                                    expense -> {
                                        wallet.getExpenses().remove(expense);
                                        wallet.setAmount(getNewAmountAfterExpenseDelete(expense, wallet));
                                        walletRepo.save(wallet);
                                    },
                                    () -> { throw new ExpenseNotFoundException(expenseId); }
                            );
                    return wallet;
                })
                .orElseThrow(() -> new UserNotFoundException("User not found with login: " + login));
    }

    @Override
    public Map<String, BigDecimal> getCountedCategories(String login, Integer id, DateRange dateRange) {
        return getExpenses(login, id, dateRange).stream()
                .filter(Predicates.isIn(dateRange).and(Predicates.IS_ELIGIBLE_EXPENSE))
                .collect(collectingAndThen(
                        groupingBy(expense -> expense.getCategory().getName(),
                                mapping(Expense::getAmount, reducing(Money.ZERO, Money::add))),
                        result -> result.entrySet().stream()
                                .collect(toMap(Map.Entry::getKey, e -> e.getValue().getAmount()))
                ));
    }

    @Override
    public List<BudgetOutputDto> getBudgets(String login, DateRange start, DateRange end) {
        DateRange fullRange = new DateRange(start.getStart(), end.getEnd());

        return getUser(login)
                .map(user -> user.getBudgets().stream()
                        .filter(Predicates.isIn(start, end))
                        .map(budget -> BudgetOutputDto.fromBudget(budget,
                                calculateCurrent(budget, getAllExpenses(user, fullRange))))
                        .toList())
                .orElseThrow(() -> new UserNotFoundException("User not found with login: " + login));
    }

    @Override
    public Integer addBudget(String login, Budget budget) {
        return getUser(login)
                .map(user -> {
                    user.getBudgets().add(budget);
                    return budgetRepo.save(budget)
                            .getId(); // Save budget and return ID
                })
                .map(id -> {
                    getUser(login).ifPresent(userRepo::save); // Save the updated user if present
                    return id;
                })
                .orElseThrow(() -> new UserNotFoundException(login)); // Handle missing user case
    }

    @Override
    public BudgetOutputDto getBudgetById(String login, Integer id) {
        return getUser(login)
                .map(user -> user.getBudgets()
                        .stream()
                        .filter(budget -> Objects.equals(budget.getId(), id))
                        .findFirst()
                        .map(budget -> BudgetOutputDto.fromBudget(budget,
                                calculateCurrent(budget, getAllExpenses(user, DateRange.withoutBounds()))))
                        .orElseThrow(() -> new IllegalArgumentException("Budget not found with ID: " + id)))
                .orElseThrow(() -> new UserNotFoundException("User not found with login: " + login));
    }


    private Money calculateCurrent(Budget budget, List<Expense> expenses) {
        return expenses
                .stream()
                .filter(Predicates.isIncludedIn(budget))
                .map(Expense::getAmount)
                .reduce(Money.ZERO, Money::add);
    }

    private Wallet getWalletByExpenseId(Integer expenseId, User user) {
        return user.getWallets()
                .stream()
                .filter(Predicates.containsExpenseWithId(expenseId))
                .findFirst()
                .orElseThrow(() -> new ExpenseNotFoundException(expenseId));
    }

    private Money getNewAmountAfterAdd(ExpenseInputDto expenseInputDto, Wallet wallet) {
        final var toAdd = expenseInputDto.amount();
        final var current = wallet.getAmount();

        return expenseInputDto.category().getProfit()
                ? current.add(toAdd)
                : current.subtract(toAdd);
    }

    private Money getNewAmountAfterExpenseDelete(Expense toDelete, Wallet wallet) {
        final var toSub = toDelete.getAmount();
        final var current = wallet.getAmount();

        return toDelete.getCategory().getProfit()
                ? current.subtract(toSub)
                : current.add(toSub);
    }


    private Optional<User> getUser(String login) {
        return Optional.ofNullable(Optional.ofNullable(login)
                        .flatMap(userRepo::findByLogin)
                        .orElseThrow(() -> new UserNotFoundException(login)));
    }

    private  WalletDto getSummaryWallet(Optional<User> user) {

        final var totalAmount = user.map(User::getWallets)
                .stream()
                .flatMap(Collection::stream)
                .map(Wallet::getAmount)
                .reduce(Money.ZERO, Money::add);

        return new WalletDto(0, totalAmount, SUMMARY_WALLET_NAME);

    }

    private List<Expense> getAllExpenses(User user, DateRange dateRange) {

        return Optional.ofNullable(user)
                .stream()
                .flatMap(u -> u.getWallets().stream())
                .map(Wallet::getExpenses)
                .flatMap(Collection::stream)
                .filter(Predicates.isIn(dateRange))
                .toList();
    }

    private List<Expense> getExpensesFromWallet(User user, Integer id, DateRange dateRange) {
        return Optional.ofNullable(getWallet(id, user))
                .map(Wallet::getExpenses)
                .stream()
                .flatMap(List::stream)
                .filter(Predicates.isIn(dateRange))
                .toList();
    }

    private Wallet getWallet(Integer id, User user) {
        return user.getWallets()
                .stream()
                .filter(wallet -> Objects.equals(wallet.getId(), id))
                .findFirst()
                .orElseThrow(() -> new WalletNotFoundException(id));
    }

    private SummaryDto calculateSummary(List<Expense> expenses) {
       final var groupedSums = expenses.stream()
                .collect(partitioningBy(
                        expense -> expense.getCategory().getProfit(),
                        reducing(Money.ZERO, Expense::getAmount, Money::add)
                ));

       final var inflow = groupedSums.getOrDefault(true, Money.ZERO);
       final var outflow = groupedSums.getOrDefault(false, Money.ZERO);

        return new SummaryDto(inflow, outflow);
    }


}
