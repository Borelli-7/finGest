package dev.kaly7.finGest.controllers.api;

import java.util.Map;

public interface ResultController {
    ResponseEntity<ResultDto> createResult(ResultDto resultDto);
    ResponseEntity<ResultDto> updateResult(ResultDto resultDto);
    ResponseEntity<String> deleteResult(Long resultID);
    ResponseEntity<ResultDto> getResultById(Long resultID);
    ResponseEntity<Map<String, Object>> listResults(boolean inflow,
                                                    boolean fixedOutflow,
                                                    String desc,
                                                    int weekNumber,
                                                    int page,
                                                    int size);
}