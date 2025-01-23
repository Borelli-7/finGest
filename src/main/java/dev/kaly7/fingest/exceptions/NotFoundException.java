package dev.kaly7.fingest.exceptions;

public abstract class NotFoundException extends ErrorResponseException {
    NotFoundException(String problem, String solution) {
        super(problem, solution);
    }
}