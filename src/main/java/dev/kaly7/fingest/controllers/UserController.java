package dev.kaly7.fingest.controllers;

import dev.kaly7.fingest.dto.UserDto;
import dev.kaly7.fingest.services.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("resources/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public List<UserDto> getUsers() {
        return userService.getUsers();
    }

    @PutMapping(value = "/{login}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateUser(@PathVariable String login,
                                           @RequestParam String field,
                                           @RequestBody Map<String, Object> value) {
        userService.updateUser(login, field, value);
        return ResponseEntity.noContent().build();
    }

}
