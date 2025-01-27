package dev.kaly7.fingest.services;

import dev.kaly7.fingest.dto.UserDto;
import dev.kaly7.fingest.dto.WalletDto;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface UserService {

    List<UserDto> getUsers();

    void updateUser(String login, String field, Map<String, Object> value);

    List<WalletDto> getWallets(String login);

    Integer addWallet(String login, @Valid WalletDto walletDto);
}
