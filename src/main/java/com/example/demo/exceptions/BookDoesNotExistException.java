package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BookDoesNotExistException extends RuntimeException {

    public BookDoesNotExistException(String message) {
        super(message);
    }
}
