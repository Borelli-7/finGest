package dev.kaly7.finGest.controllers.restImpl;

import dev.kaly7.finGest.controllers.api.ResultController;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/api/v1/result")
@AllArgsConstructor
public class ResultControllerRestImpl implements ResultController {
    private final ResultService resultService;
    @PostMapping("/create")
    public ResponseEntity<ResultDto> createResult(@RequestBody ResultDto resultDto) {
        return status(CREATED)
                .body(resultService.createResult(resultDto));
    }

    @Override
    @PutMapping("/update")
    public ResponseEntity<ResultDto> updateResult(@RequestBody ResultDto resultDto) {
        return status(OK)
                .body(resultService.updateResult(resultDto));
    }

    @Override
    @DeleteMapping("/delete/{resultID}")
    public ResponseEntity<String> deleteResult(@PathVariable Long resultID) {
        return status(OK)
                .body(resultService.deleteResult(resultID));
    }

    @Override
    @GetMapping("/findbyid/{resultID}")
    public ResponseEntity<ResultDto> getResultById(@PathVariable Long resultID) {
        return status(HttpStatus.OK).body(resultService.getResultById(resultID));
    }

    @Override
    @GetMapping("/getlist/{inflow}and{fixedOutflow}")
    public ResponseEntity<Map<String, Object>> listResults(
            @PathVariable boolean inflow,
            @PathVariable boolean fixedOutflow,
            @RequestParam(required = false) String desc,
            @RequestParam(defaultValue = "0") int weekNumber,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {

        try {

            return status(OK)
                    .body(resultService.listResults(
                            inflow, fixedOutflow, desc, weekNumber, page, size));
        }catch (Exception e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e){
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
