package dev.kaly7.fingest.exceptions;

public class UserNotFoundException extends NotFoundException {
    public UserNotFoundException(String login) {
        super("User with login " + login + " was not found.", "Check if provided login is correct and try again.");
    }
}