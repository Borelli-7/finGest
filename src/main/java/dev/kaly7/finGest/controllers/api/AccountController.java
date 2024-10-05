package dev.kaly7.finGest.controllers.api;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

public interface AccountController {

    ResponseEntity<String>  initializeAccountsUser();
    ResponseEntity<List<AccountDtoSilm>> updateAccountListUser(AccountDtoSilm... accountDtoSlimList) throws AccountNotFoundException;
    ResponseEntity<List<AccountDto>> accountsListUser();
}