package com.example.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class AccountAlreadyDeletedException extends RuntimeException {

    public AccountAlreadyDeletedException(String message) {
        super(message);
    }
}