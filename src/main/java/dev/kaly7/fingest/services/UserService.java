package dev.kaly7.fingest.services;

import dev.kaly7.fingest.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();
}
