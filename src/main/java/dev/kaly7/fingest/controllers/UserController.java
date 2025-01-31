package dev.kaly7.fingest.controllers;

import dev.kaly7.fingest.dto.*;
import dev.kaly7.fingest.entities.DateRange;
import dev.kaly7.fingest.entities.Expense;
import dev.kaly7.fingest.services.UserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.LinkedHashMap;
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
        final var wallets = userService.getWallets(login);

        return ResponseEntity.ok(wallets);
    }

    @PostMapping(value = "/{login}/wallets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Integer>> createWallet(@PathVariable String login, @RequestBody @Valid WalletDto walletDto) {
       final var id = userService.addWallet(login, walletDto);

        // Build the self and related links
        final var resource = EntityModel.of(
                id,
                linkTo(methodOn(this.getClass()).createWallet(login, walletDto)).withSelfRel(),
                linkTo(methodOn(this.getClass()).getWallets(login)).withRel("all-wallets"),
                linkTo(methodOn(this.getClass()).getSummary(login, id, null, null)).withRel("wallet-summary")
        );

        // Return response with the Location header and `_links` in the body
        final var locationLink = linkTo(methodOn(this.getClass()).getSummary(login, id, null, null)).toUri().toString();
        return ResponseEntity.created(URI.create(locationLink)).body(resource);
    }

    @GetMapping(value = "/{login}/wallets/{id}/summary", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SummaryDto> getSummary(
            @PathVariable String login,
            @PathVariable Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end) {

        final var dateRange = new DateRange(start, end);
        final var summary = userService.getSummary(login, id, dateRange);

        return ResponseEntity.ok(summary);
    }

    @GetMapping(value = "/{login}/wallets/{id}/expenses", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Expense>> getExpenses(
            @PathVariable String login,
            @PathVariable Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end) {

        final var dateRange = new DateRange(start, end);
        final var expenses = userService.getExpenses(login, id, dateRange);

        return ResponseEntity.ok(expenses);
    }

    @GetMapping(value = "/{login}/wallets/{id}/highest-expense", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Expense> getHighestExpense(
            @PathVariable String login,
            @PathVariable Integer id,
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end) {

        final var dateRange = new DateRange(start, end);
        final var highestExpense = userService.getHighestExpense(login, id, dateRange);

        return ResponseEntity.ok(highestExpense);
    }

    @PostMapping(value = "/{login}/wallets/{id}/expenses", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<LinkedHashMap<Object, Object>>> createExpense(
            @PathVariable String login,
            @PathVariable Integer id,
            @RequestBody @Valid ExpenseInputDto expense) {

        final var expenseId = userService.addExpense(login, id, expense);

        // Generate the URI for the created resource
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{expenseId}")
                .buildAndExpand(expenseId)
                .toUri();

        // Prepare response body with the expense ID
        final var responseBody = new LinkedHashMap<>();
        responseBody.put("expenseId", expenseId);

        // Create links for hypermedia support
        EntityModel<LinkedHashMap<Object, Object>> resource = EntityModel.of(responseBody,
                linkTo(methodOn(this.getClass()).createExpense(login, id, expense)).withSelfRel(),
                linkTo(methodOn(this.getClass()).getExpenses(login, id, null, null)).withRel("getExpenses"),
                linkTo(methodOn(this.getClass()).deleteExpense(login, id, expenseId)).withRel("deleteExpense")
        );

        // Return 201 Created response with the hypermedia-enabled entity
        return ResponseEntity.created(location).body(resource);
    }

    @CrossOrigin(origins = {"http://react-app:9091", "http://mobile-app:9092"})
    @DeleteMapping("/{login}/wallets/{walletId}/expenses/{expenseId}")
    public ResponseEntity<Void> deleteExpense(@PathVariable String login,
                                              @PathVariable Integer walletId,
                                              @PathVariable Integer expenseId) {
        userService.deleteExpense(login, walletId, expenseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{login}/wallets/{id}/counted-categories")
    public ResponseEntity<EntityModel<Map<String, BigDecimal>>> getCountedCategories(@PathVariable String login,
                                                                                     @PathVariable Integer id,
                                                                                     @RequestParam(required = false) String start,
                                                                                     @RequestParam(required = false) String end) {
        final var dateRange = new DateRange(start, end);
        final var countedCategories = userService.getCountedCategories(login, id, dateRange);

        // Generate HATEOAS Links
        var selfLink = linkTo(methodOn(UserController.class).getCountedCategories(login, id, start, end)).withSelfRel();
        var walletsLink = linkTo(methodOn(UserController.class).getWallets(login)).withRel("wallets");
        var expensesLink = linkTo(methodOn(UserController.class).getExpenses(login, id, start, end)).withRel("expenses");

        final var response = EntityModel.of(countedCategories, selfLink, walletsLink, expensesLink);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/{login}/budgets", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CollectionModel<EntityModel<BudgetOutputDto>>> getBudgets(
            @PathVariable String login,
            @RequestParam(name = "start_min", required = false) String startMin,
            @RequestParam(name = "start_max", required = false) String startMax,
            @RequestParam(name = "end_min", required = false) String endMin,
            @RequestParam(name = "end_max", required = false) String endMax) {

        final var startRange = new DateRange(startMin, startMax);
        final var endRange = new DateRange(endMin, endMax);

        List<BudgetOutputDto> budgets = userService.getBudgets(login, startRange, endRange);

        List<EntityModel<BudgetOutputDto>> budgetResources = budgets.stream()
                .map(budget -> EntityModel.of(budget,
                        linkTo(methodOn(UserController.class)
                                .getBudgets(login, startMin, startMax, endMin, endMax))
                                .withSelfRel(),
                        linkTo(methodOn(UserController.class)
                                .getBudgetById(login, budget.id()))
                                .withRel("budget-details")))
                .toList();

        final var response = CollectionModel.of(budgetResources,
                linkTo(methodOn(UserController.class).getBudgets(login, startMin, startMax, endMin, endMax)).withSelfRel());

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/{login}/budgets", consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Void>> createBudget(@PathVariable String login,
                                                          @Valid @RequestBody BudgetInputDto budget) {
        Integer budgetId = userService.addBudget(login, budget.toBudget());

        URI location = linkTo(methodOn(UserController.class).getBudgetById(login, budgetId)).toUri();

        EntityModel<Void> response = EntityModel.of(null,
                Link.of(location.toString(), "self"),
                linkTo(methodOn(UserController.class).getBudgets(login, null, null, null, null)).withRel("all_budgets")
        );

        return ResponseEntity.created(location).body(response);
    }


    //TODO: Implement this method
    @GetMapping(value = "/{login}/budgets/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<BudgetOutputDto>> getBudgetById(@PathVariable String login, @PathVariable Integer id) {
        BudgetOutputDto budget = userService.getBudgetById(login, id);

        EntityModel<BudgetOutputDto> resource = EntityModel.of(budget,
                linkTo(methodOn(UserController.class).getBudgetById(login, id)).withSelfRel(),
                linkTo(methodOn(UserController.class).getBudgets(login, null, null, null, null)).withRel("all_budgets")
        );

        return ResponseEntity.ok(resource);
    }


}
