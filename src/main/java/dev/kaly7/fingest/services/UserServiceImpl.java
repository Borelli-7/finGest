package dev.kaly7.fingest.services;

import dev.kaly7.fingest.db.repositories.UserRepo;
import dev.kaly7.fingest.db.repositories.WalletRepo;
import dev.kaly7.fingest.dto.UserDto;
import dev.kaly7.fingest.dto.WalletDto;
import dev.kaly7.fingest.entities.Wallet;
import dev.kaly7.fingest.entities.money.Money;
import org.apache.commons.beanutils.PropertyUtils;
import dev.kaly7.fingest.entities.User;
import dev.kaly7.fingest.exceptions.UpdateFieldException;
import dev.kaly7.fingest.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private final static String SUMMARY_WALLET_NAME = "summary";

    private final UserRepo userRepo;
    private final WalletRepo walletRepo;

    public UserServiceImpl(UserRepo userRepo, WalletRepo walletRepo) {
        this.userRepo = userRepo;
        this.walletRepo = walletRepo;
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
        Optional<User> user = getUser(login);

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


    private Optional<User> getUser(String login) {
        return Optional.ofNullable(Optional.ofNullable(login)
                        .flatMap(userRepo::findByLogin)
                        .orElseThrow(() -> new UserNotFoundException(login)));
    }

    private WalletDto getSummaryWallet(Optional<User> user) {

        var totalAmount = Optional.ofNullable(user.get().getWallets())
                .stream()
                .flatMap(Collection::stream)
                .map(Wallet::getAmount)
                .reduce(Money.ZERO, Money::add);

        return new WalletDto(0, totalAmount, SUMMARY_WALLET_NAME);

    }


}
