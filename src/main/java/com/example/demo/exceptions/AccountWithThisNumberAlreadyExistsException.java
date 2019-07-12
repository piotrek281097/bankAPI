package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class AccountWithThisNumberAlreadyExistsException extends RuntimeException {

    public AccountWithThisNumberAlreadyExistsException(String message) {
        super(message);
    }
}
