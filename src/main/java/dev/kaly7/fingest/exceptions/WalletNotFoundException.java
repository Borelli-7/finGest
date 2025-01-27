package dev.kaly7.fingest.exceptions;

public class WalletNotFoundException extends NotFoundException {
    public WalletNotFoundException(Integer id) {
        super("Wallet with id " + id + " was not found.", "Check if provided id is correct and try again.");
    }
}
