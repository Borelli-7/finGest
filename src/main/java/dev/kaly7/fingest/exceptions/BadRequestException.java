package dev.kaly7.fingest.exceptions;

import org.springframework.http.ResponseEntity;

public abstract class BadRequestException extends ErrorResponseException {

    BadRequestException(String problem, String solution) {
        super(problem, solution);
    }

    public ResponseEntity<Problem> getResponseEntity() {
        return ResponseEntity.badRequest().body(getProblem());
    }
}