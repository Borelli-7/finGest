package dev.kaly7.fingest.exceptions;

public abstract class ErrorResponseException extends RuntimeException {

    private final Problem problem;

    protected ErrorResponseException(String problem, String solution) {
        super(problem);
        this.problem = new Problem(problem, solution);
    }

    public Problem getProblem() {
        return problem;
    }
}

