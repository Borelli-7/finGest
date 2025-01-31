package dev.kaly7.fingest.dto;

import dev.kaly7.fingest.entities.Wallet;
import dev.kaly7.fingest.entities.money.Money;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record WalletDto(
        Integer id,
        @NotNull @Valid Money amount,
        @NotNull String name
) {

    public static WalletDto fromWallet(Wallet wallet) {
        return new WalletDto(wallet.getId(), wallet.getAmount(), wallet.getName());
    }

    public Wallet toWallet() {
        return Wallet.builder()
                .id(id)
                .amount(amount)
                .name(name)
                .build();
    }
}

