package dev.kaly7.fingest.services;

import dev.kaly7.fingest.common.validation.Predicates;
import dev.kaly7.fingest.db.repositories.ExpenseRepo;
import dev.kaly7.fingest.db.repositories.UserRepo;
import dev.kaly7.fingest.db.repositories.WalletRepo;
import dev.kaly7.fingest.dto.ExpenseInputDto;
import dev.kaly7.fingest.dto.SummaryDto;
import dev.kaly7.fingest.dto.UserDto;
import dev.kaly7.fingest.dto.WalletDto;
import dev.kaly7.fingest.entities.DateRange;
import dev.kaly7.fingest.entities.Expense;
import dev.kaly7.fingest.entities.Wallet;
import dev.kaly7.fingest.entities.money.Money;
import dev.kaly7.fingest.exceptions.ExpenseNotFoundException;
import dev.kaly7.fingest.exceptions.WalletNotFoundException;
import org.apache.commons.beanutils.PropertyUtils;
import dev.kaly7.fingest.entities.User;
import dev.kaly7.fingest.exceptions.UpdateFieldException;
import dev.kaly7.fingest.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Stream;

import static dev.kaly7.fingest.common.validation.Predicates.containsExpenseWithId;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.reducing;

@Service
public class UserServiceImpl implements UserService {

    private static final String SUMMARY_WALLET_NAME = "summary";

    private final UserRepo userRepo;
    private final WalletRepo walletRepo;
    private final ExpenseRepo expenseRepo;

    public UserServiceImpl(UserRepo userRepo, WalletRepo walletRepo, ExpenseRepo expenseRepo) {
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
        this.expenseRepo = expenseRepo;
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

        return Optional.of(getUser(login).get())
                .map(user -> switch (id) {
                    case 0 -> getAllExpenses(user, dateRange);
                    default -> getExpensesFromWallet(user, id, dateRange);
                })
                .orElseThrow(() -> new IllegalArgumentException("User not found for login: " + login));
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

        var totalAmount = Optional.ofNullable(user.get().getWallets())
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
