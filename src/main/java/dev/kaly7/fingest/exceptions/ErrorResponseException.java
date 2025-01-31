package dev.kaly7.fingest.exceptions;

import lombok.Getter;

@Getter
public abstract class ErrorResponseException extends RuntimeException {

    private final Problem problem;

    protected ErrorResponseException(String problem, String solution) {
        super(problem);
        this.problem = new Problem(problem, solution);
    }

}

