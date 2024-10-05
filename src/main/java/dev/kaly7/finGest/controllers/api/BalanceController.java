package dev.kaly7.finGest.controllers.api;

import java.util.Map;

public interface BalanceController {
    ResponseEntity<BalanceDto> createBalance(BalanceDto balanceDto);
    ResponseEntity<BalanceDto> updateBalance(BalanceDto balanceDto);
    ResponseEntity<String> deleteBalance(Long balanceID);
    ResponseEntity<BalanceDto> getBalanceById(Long balanceID);
    ResponseEntity<Map<String, Object>> listBalances(boolean assets,
                                                     String desc,
                                                     double price,
                                                     int page,
                                                     int size);
}