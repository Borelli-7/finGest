package dev.kaly7.fingest.controllers;

import dev.kaly7.fingest.dto.SummaryDto;
import dev.kaly7.fingest.dto.UserDto;
import dev.kaly7.fingest.dto.WalletDto;
import dev.kaly7.fingest.entities.DateRange;
import dev.kaly7.fingest.services.UserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
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

    @GetMapping(value = "/{login}/wallets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<WalletDto>> getWallets(@PathVariable String login) {
        var wallets = userService.getWallets(login);

        return ResponseEntity.ok(wallets);
    }

    @PostMapping(value = "/{login}/wallets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Integer>> createWallet(@PathVariable String login, @RequestBody @Valid WalletDto walletDto) {
        Integer id = userService.addWallet(login, walletDto);

        // Build the self and related links
        EntityModel<Integer> resource = EntityModel.of(
                id,
                linkTo(methodOn(this.getClass()).createWallet(login, walletDto)).withSelfRel(),
                linkTo(methodOn(this.getClass()).getWallets(login)).withRel("all-wallets"),
                linkTo(methodOn(this.getClass()).getSummary(login, id, null, null)).withRel("wallet-summary")
        );

        // Return response with the Location header and `_links` in the body
        String locationLink = linkTo(methodOn(this.getClass()).getSummary(login, id, null, null)).toUri().toString();
        return ResponseEntity.created(URI.create(locationLink)).body(resource);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SummaryDto> getSummary(
            @PathVariable String login,
            @PathVariable Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end) {

        var dateRange = new DateRange(start, end);
        var summary = userService.getSummary(login, id, dateRange);

        return ResponseEntity.ok(summary);
    }


}
