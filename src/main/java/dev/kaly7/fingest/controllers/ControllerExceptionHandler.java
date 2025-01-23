package dev.kaly7.fingest.controllers;

import dev.kaly7.fingest.exceptions.BadRequestException;
import dev.kaly7.fingest.exceptions.NotFoundException;
import dev.kaly7.fingest.exceptions.Problem;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
class ControllerExceptionHandler {

    @ExceptionHandler({BadRequestException.class, NotFoundException.class})
    @ResponseBody
    public Problem handleException(Exception ex) {
        if (ex instanceof BadRequestException) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, null, ex);
        } else if (ex instanceof NotFoundException) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, null, ex);
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred");
    }
}
