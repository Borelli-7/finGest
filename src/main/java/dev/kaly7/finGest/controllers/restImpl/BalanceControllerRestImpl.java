package dev.kaly7.finGest.controllers.restImpl;

import dev.kaly7.finGest.controllers.api.BalanceController;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;
@RestController
@RequestMapping("/api/v1/balance")
@AllArgsConstructor
public class BalanceControllerRestImpl implements BalanceController {
    private final BalanceService balanceService;
    @Override
    @PostMapping("/create")
    public ResponseEntity<BalanceDto> createBalance(@RequestBody BalanceDto balanceDto) {
        return status(CREATED)
                .body(balanceService.createBalance(balanceDto));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<BalanceDto> updateBalance(@RequestBody BalanceDto balanceDto) {
        return status(OK)
                .body(balanceService.updateBalance(balanceDto));
    }

    @Override
    @DeleteMapping("/delete/{balanceID}")
    public ResponseEntity<String> deleteBalance(@PathVariable Long balanceID) {
        return status(OK)
                .body(balanceService.deleteBalance(balanceID));
    }

    @Override
    @GetMapping("/findbyid/{balanceID}")
    public ResponseEntity<BalanceDto> getBalanceById(@PathVariable Long balanceID) {
        return status(HttpStatus.OK)
                .body(balanceService.getBalanceById(balanceID));
    }

    @Override
    @GetMapping("/getlist/{assets}")
    public ResponseEntity<Map<String, Object>> listBalances(
            @PathVariable boolean assets,
            @RequestParam(required = false) String desc,
            @RequestParam(defaultValue = "0")double price,
            @RequestParam(defaultValue = "0")int page,
            @RequestParam(defaultValue = "3") int size) {

        try {

            return status(OK)
                    .body(balanceService.listBalances(
                            assets, desc, price, page, size));
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
