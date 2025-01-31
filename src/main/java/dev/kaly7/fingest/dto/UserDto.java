package dev.kaly7.fingest.dto;

import dev.kaly7.fingest.entities.User;

public record UserDto(String login, String firstName, String lastName, boolean admin) {

    public static UserDto fromUser(User user) {
        return new UserDto(
                user.getLogin(),
                user.getFirstName(),
                user.getLastName(),
                user.isAdmin()
        );
    }
}

