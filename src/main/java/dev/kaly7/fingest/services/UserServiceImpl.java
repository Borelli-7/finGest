package dev.kaly7.fingest.services;

import dev.kaly7.fingest.db.repositories.UserRepo;
import dev.kaly7.fingest.dto.UserDto;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;

    public UserServiceImpl(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepo.findAll()
                        .stream()
                        .map(UserDto::fromUser)
                        .toList();
    }
}
