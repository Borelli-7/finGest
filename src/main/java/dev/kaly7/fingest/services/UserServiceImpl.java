package dev.kaly7.fingest.services;

import dev.kaly7.fingest.db.repositories.UserRepo;
import dev.kaly7.fingest.dto.UserDto;
import org.apache.commons.beanutils.PropertyUtils;
import dev.kaly7.fingest.entities.User;
import dev.kaly7.fingest.exceptions.UpdateFieldException;
import dev.kaly7.fingest.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    private Optional<User> getUser(String login) {
        return Optional.ofNullable(Optional.ofNullable(login)
                        .flatMap(userRepo::findByLogin)
                        .orElseThrow(() -> new UserNotFoundException(login)));
    }

}
