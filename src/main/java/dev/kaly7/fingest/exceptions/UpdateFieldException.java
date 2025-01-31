package dev.kaly7.fingest.exceptions;

public class UpdateFieldException extends BadRequestException {
    public UpdateFieldException(String problem, String solution) {
        super(problem, solution);
    }
}